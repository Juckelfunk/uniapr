// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.classpath;

import org.pitest.classinfo.NameToClassInfo;
import org.pitest.classinfo.TestToClassMapper;
import org.pitest.functional.Option;
import org.pitest.functional.prelude.Prelude;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import org.pitest.functional.F;
import org.pitest.classinfo.ClassName;
import org.pitest.functional.FCollection;
import org.pitest.classinfo.ClassInfo;
import java.util.Collection;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.classinfo.Repository;
import org.pitest.classinfo.ClassInfoSource;

public class CodeSource implements ClassInfoSource
{
    private final ProjectClassPaths classPath;
    private final Repository classRepository;
    
    public CodeSource(final ProjectClassPaths classPath) {
        this(classPath, new Repository(new ClassPathByteArraySource(classPath.getClassPath())));
    }
    
    CodeSource(final ProjectClassPaths classPath, final Repository classRepository) {
        this.classPath = classPath;
        this.classRepository = classRepository;
    }
    
    public Collection<ClassInfo> getCode() {
        return (Collection<ClassInfo>)FCollection.flatMap((Iterable<? extends ClassName>)this.classPath.code(), (F<ClassName, ? extends Iterable<Object>>)this.nameToClassInfo());
    }
    
    public Set<ClassName> getCodeUnderTestNames() {
        final Set<ClassName> codeClasses = new HashSet<ClassName>();
        FCollection.mapTo(this.getCode(), ClassInfo.toClassName(), codeClasses);
        return codeClasses;
    }
    
    public List<ClassInfo> getTests() {
        return (List<ClassInfo>)FCollection.flatMap((Iterable<? extends ClassName>)this.classPath.test(), (F<ClassName, ? extends Iterable<Object>>)this.nameToClassInfo()).filter((F<Object, Boolean>)Prelude.not(ClassInfo.matchIfAbstract()));
    }
    
    public ClassPath getClassPath() {
        return this.classPath.getClassPath();
    }
    
    public ProjectClassPaths getProjectPaths() {
        return this.classPath;
    }
    
    public Option<ClassName> findTestee(final String className) {
        final TestToClassMapper mapper = new TestToClassMapper(this.classRepository);
        return mapper.findTestee(className);
    }
    
    public Collection<ClassInfo> getClassInfo(final Collection<ClassName> classes) {
        return (Collection<ClassInfo>)FCollection.flatMap((Iterable<? extends ClassName>)classes, (F<ClassName, ? extends Iterable<Object>>)this.nameToClassInfo());
    }
    
    public Option<byte[]> fetchClassBytes(final ClassName clazz) {
        return this.classRepository.querySource(clazz);
    }
    
    @Override
    public Option<ClassInfo> fetchClass(final ClassName clazz) {
        return this.classRepository.fetchClass(clazz);
    }
    
    private F<ClassName, Option<ClassInfo>> nameToClassInfo() {
        return new NameToClassInfo(this.classRepository);
    }
}
