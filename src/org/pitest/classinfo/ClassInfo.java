// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.classinfo;

import org.pitest.functional.F;
import java.math.BigInteger;
import java.lang.annotation.Annotation;
import org.pitest.functional.Option;
import org.pitest.functional.FCollection;
import java.util.Map;
import java.util.Collection;
import java.util.Set;

public class ClassInfo
{
    private final ClassIdentifier id;
    private final int access;
    private final Set<Integer> codeLines;
    private final ClassPointer outerClass;
    private final ClassPointer superClass;
    private final Collection<ClassName> annotations;
    private final String sourceFile;
    private final Map<ClassName, Object> classAnnotationValues;
    
    public ClassInfo(final ClassPointer superClass, final ClassPointer outerClass, final ClassInfoBuilder builder) {
        this.superClass = superClass;
        this.outerClass = outerClass;
        this.id = builder.id;
        this.access = builder.access;
        this.codeLines = builder.codeLines;
        this.annotations = FCollection.map(builder.annotations, ClassName.stringToClassName());
        this.sourceFile = builder.sourceFile;
        this.classAnnotationValues = builder.classAnnotationValues;
    }
    
    public int getNumberOfCodeLines() {
        return this.codeLines.size();
    }
    
    public boolean isCodeLine(final int line) {
        return this.codeLines.contains(line);
    }
    
    public ClassIdentifier getId() {
        return this.id;
    }
    
    public ClassName getName() {
        return this.id.getName();
    }
    
    public boolean isInterface() {
        return (this.access & 0x200) != 0x0;
    }
    
    public boolean isAbstract() {
        return (this.access & 0x400) != 0x0;
    }
    
    public boolean isSynthetic() {
        return (this.access & 0x1000) != 0x0;
    }
    
    public boolean isTopLevelClass() {
        return this.getOuterClass().hasNone();
    }
    
    public Option<ClassInfo> getOuterClass() {
        return this.outerClass.fetch();
    }
    
    public Option<ClassInfo> getSuperClass() {
        return this.getParent();
    }
    
    public String getSourceFileName() {
        return this.sourceFile;
    }
    
    public boolean hasAnnotation(final Class<? extends Annotation> annotation) {
        return this.hasAnnotation(ClassName.fromClass(annotation));
    }
    
    public boolean hasAnnotation(final ClassName annotation) {
        return this.annotations.contains(annotation);
    }
    
    public Object getClassAnnotationValue(final ClassName annotation) {
        return this.classAnnotationValues.get(annotation);
    }
    
    public boolean descendsFrom(final Class<?> clazz) {
        return this.descendsFrom(ClassName.fromClass(clazz));
    }
    
    public HierarchicalClassId getHierarchicalId() {
        return new HierarchicalClassId(this.id, this.getDeepHash());
    }
    
    public BigInteger getDeepHash() {
        BigInteger hash = this.getHash();
        final Option<ClassInfo> parent = this.getParent();
        if (parent.hasSome()) {
            hash = hash.add(parent.value().getHash());
        }
        final Option<ClassInfo> outer = this.getOuterClass();
        if (outer.hasSome()) {
            hash = hash.add(outer.value().getHash());
        }
        return hash;
    }
    
    public BigInteger getHash() {
        return BigInteger.valueOf(this.id.getHash());
    }
    
    private Option<ClassInfo> getParent() {
        if (this.superClass == null) {
            return (Option<ClassInfo>)Option.none();
        }
        return this.superClass.fetch();
    }
    
    private boolean descendsFrom(final ClassName clazz) {
        return !this.getSuperClass().hasNone() && (this.getSuperClass().value().getName().equals(clazz) || this.getSuperClass().value().descendsFrom(clazz));
    }
    
    public static F<ClassInfo, Boolean> matchIfAbstract() {
        return new F<ClassInfo, Boolean>() {
            @Override
            public Boolean apply(final ClassInfo a) {
                return a.isAbstract();
            }
        };
    }
    
    @Override
    public String toString() {
        return this.id.getName().asJavaName();
    }
    
    public static F<ClassInfo, ClassName> toClassName() {
        return new F<ClassInfo, ClassName>() {
            @Override
            public ClassName apply(final ClassInfo a) {
                return a.getName();
            }
        };
    }
    
    public static F<ClassInfo, HierarchicalClassId> toFullClassId() {
        return new F<ClassInfo, HierarchicalClassId>() {
            @Override
            public HierarchicalClassId apply(final ClassInfo a) {
                return a.getHierarchicalId();
            }
        };
    }
}
