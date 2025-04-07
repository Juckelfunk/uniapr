// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.classinfo;

import org.pitest.reloc.asm.Type;
import java.util.List;
import java.util.ArrayList;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.AnnotationVisitor;
import org.pitest.bytecode.NullVisitor;
import org.pitest.reloc.asm.ClassReader;
import org.pitest.functional.F5;
import org.pitest.reloc.asm.ClassVisitor;

public final class ClassInfoVisitor extends MethodFilteringAdapter
{
    private final ClassInfoBuilder classInfo;
    
    private ClassInfoVisitor(final ClassInfoBuilder classInfo, final ClassVisitor writer) {
        super(writer, BridgeMethodFilter.INSTANCE);
        this.classInfo = classInfo;
    }
    
    public static ClassInfoBuilder getClassInfo(final ClassName name, final byte[] bytes, final long hash) {
        final ClassReader reader = new ClassReader(bytes);
        final ClassVisitor writer = new NullVisitor();
        final ClassInfoBuilder info = new ClassInfoBuilder();
        info.id = new ClassIdentifier(hash, name);
        reader.accept(new ClassInfoVisitor(info, writer), 0);
        return info;
    }
    
    @Override
    public void visitSource(final String source, final String debug) {
        super.visitSource(source, debug);
        this.classInfo.sourceFile = source;
    }
    
    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.classInfo.access = access;
        this.classInfo.superClass = superName;
    }
    
    @Override
    public void visitOuterClass(final String owner, final String name, final String desc) {
        super.visitOuterClass(owner, name, desc);
        this.classInfo.outerClass = owner;
    }
    
    @Override
    public void visitInnerClass(final String name, final String outerName, final String innerName, final int access) {
        super.visitInnerClass(name, outerName, innerName, access);
        if (outerName != null && this.classInfo.id.getName().equals(ClassName.fromString(name))) {
            this.classInfo.outerClass = outerName;
        }
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        final String type = desc.substring(1, desc.length() - 1);
        this.classInfo.registerAnnotation(type);
        return new ClassAnnotationValueVisitor(this.classInfo, ClassName.fromString(type));
    }
    
    @Override
    public MethodVisitor visitMethodIfRequired(final int access, final String name, final String desc, final String signature, final String[] exceptions, final MethodVisitor methodVisitor) {
        return new InfoMethodVisitor(this.classInfo, methodVisitor);
    }
    
    private static class ClassAnnotationValueVisitor extends AnnotationVisitor
    {
        private final ClassInfoBuilder classInfo;
        private final ClassName annotation;
        
        ClassAnnotationValueVisitor(final ClassInfoBuilder classInfo, final ClassName annotation) {
            super(393216, null);
            this.classInfo = classInfo;
            this.annotation = annotation;
        }
        
        @Override
        public void visit(final String name, final Object value) {
            if (name.equals("value")) {
                this.classInfo.registerClassAnnotationValue(this.annotation, simplify(value));
            }
            super.visit(name, value);
        }
        
        @Override
        public AnnotationVisitor visitArray(final String name) {
            if (name.equals("value")) {
                final List<Object> arrayValue = new ArrayList<Object>();
                return new AnnotationVisitor(393216, null) {
                    @Override
                    public void visit(final String name, final Object value) {
                        arrayValue.add(simplify(value));
                        super.visit(name, value);
                    }
                    
                    @Override
                    public void visitEnd() {
                        ClassAnnotationValueVisitor.this.classInfo.registerClassAnnotationValue(ClassAnnotationValueVisitor.this.annotation, arrayValue.toArray());
                    }
                };
            }
            return super.visitArray(name);
        }
        
        private static Object simplify(final Object value) {
            Object newValue = value;
            if (value instanceof Type) {
                newValue = ((Type)value).getClassName();
            }
            return newValue;
        }
    }
}
