// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr.jvm.core;

import java.util.Iterator;
import java.util.List;
import org.uniapr.jvm.agent.JVMClinitClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.MethodVisitor;

class AccessRewriterMethodVisitor extends MethodVisitor implements Opcodes
{
    String slashClazzName;
    
    public AccessRewriterMethodVisitor(final MethodVisitor mv, final String clazzName) {
        super(327680, mv);
        this.slashClazzName = clazzName;
    }
    
    @Override
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
        if (opcode == 184 || (opcode == 183 && name.equals("<init>"))) {
            if (JVMClinitClassTransformer.leakingClassMap.containsKey(owner)) {
                final List<String> ancestors = JVMClinitClassTransformer.leakingClassMap.get(owner);
                for (final String ancestor : ancestors) {
                    if (!ancestor.equals(this.slashClazzName)) {
                        this.mv.visitMethodInsn(184, ancestor, "prapr_reclinit", "()V", false);
                    }
                }
            }
            if (JVMClinitClassTransformer.leakingClasses.keySet().contains(owner) && (!owner.equals(this.slashClazzName) || owner.contains("Test")) && !JVMClinitClassTransformer.leakingClasses.get(owner) && !this.isSynthetic(name)) {
                this.mv.visitMethodInsn(184, owner, "prapr_reclinit", "()V", false);
            }
        }
        this.mv.visitMethodInsn(opcode, owner, name, desc, itf);
    }
    
    @Override
    public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
        if (opcode == 178 || opcode == 179) {
            if (JVMClinitClassTransformer.leakingClassMap.containsKey(owner)) {
                final List<String> ancestors = JVMClinitClassTransformer.leakingClassMap.get(owner);
                for (final String ancestor : ancestors) {
                    if (!ancestor.equals(this.slashClazzName)) {
                        this.mv.visitMethodInsn(184, ancestor, "prapr_reclinit", "()V", false);
                    }
                }
            }
            if (JVMClinitClassTransformer.leakingClasses.keySet().contains(owner) && !JVMClinitClassTransformer.leakingClasses.get(owner) && (!owner.equals(this.slashClazzName) || owner.contains("Test"))) {
                this.mv.visitMethodInsn(184, owner, "prapr_reclinit", "()V", false);
            }
        }
        this.mv.visitFieldInsn(opcode, owner, name, desc);
    }
    
    public static String strip(final String s) {
        return s.substring(1, s.length() - 1);
    }
    
    @Override
    public void visitMaxs(final int maxStack, final int maxLocals) {
        this.mv.visitMaxs(maxStack + 4, maxLocals);
    }
    
    public boolean isVirtual(final int access) {
        return 0x0 == (access & 0x8);
    }
    
    public boolean isSynthetic(final String name) {
        return name.contains("$");
    }
}
