// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr.jvm.core;

import org.objectweb.asm.FieldVisitor;
import org.uniapr.jvm.agent.JVMClinitClassTransformer;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.ClassVisitor;

public class NormalAccessTransformVisitor extends ClassVisitor implements Opcodes
{
    String slashClazzName;
    boolean clinit;
    boolean isInterface;
    int id;
    
    public NormalAccessTransformVisitor(final String clazzName, final ClassVisitor cv, final boolean isInterface, final int id) {
        super(327680, cv);
        this.clinit = false;
        this.isInterface = false;
        this.slashClazzName = clazzName;
        this.isInterface = isInterface;
        this.id = id;
        cv.visitSource("prapr_gen", "prapr_gen");
    }
    
    @Override
    public void visitSource(final String s1, final String debug) {
        this.cv.visitSource("prapr_gen-" + s1, "prapr_gen");
    }
    
    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        MethodVisitor mv = this.cv.visitMethod(access, name, desc, signature, exceptions);
        final boolean isClinit = name.startsWith("<clinit>");
        if (isClinit) {
            this.clinit = true;
            if (!this.isInterface && JVMClinitClassTransformer.leakingClasses.keySet().contains(this.slashClazzName)) {
                this.addNewClinit(mv, access, name, desc, signature, exceptions);
                mv = this.cv.visitMethod(access + 1, "prapr_reclinit", desc, signature, exceptions);
                mv = ((mv == null) ? null : new ClinitRewriterMethodVisitor(mv, this.slashClazzName, this.id));
            }
        }
        return (mv == null) ? null : new AccessRewriterMethodVisitor(mv, this.slashClazzName);
    }
    
    @Override
    public FieldVisitor visitField(final int access, final String name, final String descriptor, final String signature, final Object value) {
        if (this.isSafe(access, value) || !JVMClinitClassTransformer.leakingClasses.keySet().contains(this.slashClazzName)) {
            return this.cv.visitField(access, name, descriptor, signature, value);
        }
        if (this.isInterface) {
            return this.cv.visitField(access, name, descriptor, signature, value);
        }
        if ((access & 0x10) != 0x0) {
            return this.cv.visitField(access - 16, name, descriptor, signature, value);
        }
        return this.cv.visitField(access, name, descriptor, signature, value);
    }
    
    public boolean isSafe(final int access, final Object value) {
        return (access & 0x8) == 0x0 || ((access & 0x10) != 0x0 && value != null);
    }
    
    public void addNewClinit(final MethodVisitor mv, final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        mv.visitCode();
        mv.visitMethodInsn(184, this.slashClazzName, "prapr_reclinit", desc, false);
        mv.visitInsn(177);
        mv.visitEnd();
    }
}
