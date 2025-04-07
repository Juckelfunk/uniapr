// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr.jvm.core;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.MethodVisitor;

class ClinitRewriterMethodVisitor extends MethodVisitor implements Opcodes
{
    String slashClazzName;
    int id;
    
    public ClinitRewriterMethodVisitor(final MethodVisitor mv, final String clazzName, final int id) {
        super(327680, mv);
        this.slashClazzName = clazzName;
        this.id = id;
    }
    
    @Override
    public void visitCode() {
        this.mv.visitLdcInsn(this.id);
        this.mv.visitMethodInsn(184, "org/uniapr/jvm/core/JVMStatus", "lockedCheck", "(I)Z", false);
        final Label l0 = new Label();
        this.mv.visitJumpInsn(153, l0);
        this.mv.visitInsn(177);
        this.mv.visitLabel(l0);
        super.visitCode();
    }
    
    @Override
    public void visitMaxs(final int maxStack, final int maxLocals) {
        this.mv.visitMaxs(maxStack + 4, maxLocals);
    }
    
    public boolean isVirtual(final int access) {
        return 0x0 == (access & 0x8);
    }
}
