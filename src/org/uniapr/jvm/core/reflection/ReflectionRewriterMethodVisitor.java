// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr.jvm.core.reflection;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.MethodVisitor;

class ReflectionRewriterMethodVisitor extends MethodVisitor implements Opcodes
{
    String slashClazzName;
    
    public ReflectionRewriterMethodVisitor(final MethodVisitor mv, final String clazzName) {
        super(327680, mv);
        this.slashClazzName = clazzName;
    }
    
    @Override
    public void visitCode() {
        this.mv.visitVarInsn(25, 0);
        this.mv.visitFieldInsn(180, this.slashClazzName, "clazz", "Ljava/lang/Class;");
        this.mv.visitMethodInsn(184, "org/uniapr/jvm/core/JVMStatus", "reflectionLockedCheck", "(Ljava/lang/Class;)V", false);
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
