// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.reloc.asm.commons;

import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.FieldVisitor;
import org.pitest.reloc.asm.TypePath;
import org.pitest.reloc.asm.AnnotationVisitor;
import org.pitest.reloc.asm.ModuleVisitor;
import org.pitest.reloc.asm.ClassVisitor;

@Deprecated
public class RemappingClassAdapter extends ClassVisitor
{
    protected final Remapper remapper;
    protected String className;
    
    public RemappingClassAdapter(final ClassVisitor cv, final Remapper remapper) {
        this(393216, cv, remapper);
    }
    
    protected RemappingClassAdapter(final int api, final ClassVisitor cv, final Remapper remapper) {
        super(api, cv);
        this.remapper = remapper;
    }
    
    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        this.className = name;
        super.visit(version, access, this.remapper.mapType(name), this.remapper.mapSignature(signature, false), this.remapper.mapType(superName), (String[])((interfaces == null) ? null : this.remapper.mapTypes(interfaces)));
    }
    
    @Override
    public ModuleVisitor visitModule(final String name, final int flags, final String version) {
        throw new RuntimeException("RemappingClassAdapter is deprecated, use ClassRemapper instead");
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        final AnnotationVisitor av = super.visitAnnotation(this.remapper.mapDesc(desc), visible);
        return (av == null) ? null : this.createRemappingAnnotationAdapter(av);
    }
    
    @Override
    public AnnotationVisitor visitTypeAnnotation(final int typeRef, final TypePath typePath, final String desc, final boolean visible) {
        final AnnotationVisitor av = super.visitTypeAnnotation(typeRef, typePath, this.remapper.mapDesc(desc), visible);
        return (av == null) ? null : this.createRemappingAnnotationAdapter(av);
    }
    
    @Override
    public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
        final FieldVisitor fv = super.visitField(access, this.remapper.mapFieldName(this.className, name, desc), this.remapper.mapDesc(desc), this.remapper.mapSignature(signature, true), this.remapper.mapValue(value));
        return (fv == null) ? null : this.createRemappingFieldAdapter(fv);
    }
    
    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        final String newDesc = this.remapper.mapMethodDesc(desc);
        final MethodVisitor mv = super.visitMethod(access, this.remapper.mapMethodName(this.className, name, desc), newDesc, this.remapper.mapSignature(signature, false), (String[])((exceptions == null) ? null : this.remapper.mapTypes(exceptions)));
        return (mv == null) ? null : this.createRemappingMethodAdapter(access, newDesc, mv);
    }
    
    @Override
    public void visitInnerClass(final String name, final String outerName, final String innerName, final int access) {
        super.visitInnerClass(this.remapper.mapType(name), (outerName == null) ? null : this.remapper.mapType(outerName), innerName, access);
    }
    
    @Override
    public void visitOuterClass(final String owner, final String name, final String desc) {
        super.visitOuterClass(this.remapper.mapType(owner), (name == null) ? null : this.remapper.mapMethodName(owner, name, desc), (desc == null) ? null : this.remapper.mapMethodDesc(desc));
    }
    
    protected FieldVisitor createRemappingFieldAdapter(final FieldVisitor fv) {
        return new RemappingFieldAdapter(fv, this.remapper);
    }
    
    protected MethodVisitor createRemappingMethodAdapter(final int access, final String newDesc, final MethodVisitor mv) {
        return new RemappingMethodAdapter(access, newDesc, mv, this.remapper);
    }
    
    protected AnnotationVisitor createRemappingAnnotationAdapter(final AnnotationVisitor av) {
        return new RemappingAnnotationAdapter(av, this.remapper);
    }
}
