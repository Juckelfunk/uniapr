// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.classinfo;

import org.pitest.functional.Option;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;

public class Repository implements ClassInfoSource
{
    private final HashFunction hashFunction;
    private final Map<ClassName, ClassInfo> knownClasses;
    private final Set<ClassName> unknownClasses;
    private final ClassByteArraySource source;
    
    public Repository(final ClassByteArraySource source) {
        this(source, new AddlerHash());
    }
    
    Repository(final ClassByteArraySource source, final HashFunction hashFunction) {
        this.knownClasses = new HashMap<ClassName, ClassInfo>();
        this.unknownClasses = new HashSet<ClassName>();
        this.source = source;
        this.hashFunction = hashFunction;
    }
    
    public boolean hasClass(final ClassName name) {
        return this.knownClasses.containsKey(name) || this.querySource(name).hasSome();
    }
    
    public Option<ClassInfo> fetchClass(final Class<?> clazz) {
        return this.fetchClass(clazz.getName());
    }
    
    private Option<ClassInfo> fetchClass(final String name) {
        return this.fetchClass(ClassName.fromString(name));
    }
    
    @Override
    public Option<ClassInfo> fetchClass(final ClassName name) {
        final ClassInfo info = this.knownClasses.get(name);
        if (info != null) {
            return Option.some(info);
        }
        final Option<ClassInfo> maybeInfo = this.nameToClassInfo(name);
        if (maybeInfo.hasSome()) {
            this.knownClasses.put(name, maybeInfo.value());
        }
        return maybeInfo;
    }
    
    private Option<ClassInfo> nameToClassInfo(final ClassName name) {
        final Option<byte[]> bytes = this.querySource(name);
        if (bytes.hasSome()) {
            final ClassInfoBuilder classData = ClassInfoVisitor.getClassInfo(name, bytes.value(), this.hashFunction.hash(bytes.value()));
            return this.contructClassInfo(classData);
        }
        return (Option<ClassInfo>)Option.none();
    }
    
    public Option<byte[]> querySource(final ClassName name) {
        if (this.unknownClasses.contains(name)) {
            return (Option<byte[]>)Option.none();
        }
        final Option<byte[]> option = this.source.getBytes(name.asJavaName());
        if (option.hasSome()) {
            return option;
        }
        this.unknownClasses.add(name);
        return option;
    }
    
    private Option<ClassInfo> contructClassInfo(final ClassInfoBuilder classData) {
        return Option.some(new ClassInfo(this.resolveClass(classData.superClass), this.resolveClass(classData.outerClass), classData));
    }
    
    private ClassPointer resolveClass(final String clazz) {
        if (clazz == null) {
            return new DefaultClassPointer(null);
        }
        final ClassInfo alreadyResolved = this.knownClasses.get(ClassName.fromString(clazz));
        if (alreadyResolved != null) {
            return new DefaultClassPointer(alreadyResolved);
        }
        return new DeferredClassPointer(this, ClassName.fromString(clazz));
    }
}
