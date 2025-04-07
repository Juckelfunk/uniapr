// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.mutators;

import org.pitest.reloc.asm.Label;
import org.pitest.reloc.asm.Handle;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import org.pitest.reloc.asm.MethodVisitor;

class MutateEveryThing extends MethodVisitor
{
    private final MethodMutatorFactory factory;
    private final MutationContext context;
    
    MutateEveryThing(final MethodMutatorFactory factory, final MutationContext context, final MethodVisitor delegateMethodVisitor) {
        super(393216, delegateMethodVisitor);
        this.factory = factory;
        this.context = context;
    }
    
    @Override
    public void visitIincInsn(final int var, final int increment) {
        this.mutate("visitIincInsn");
    }
    
    @Override
    public void visitInsn(final int opcode) {
        if (opcode != 177) {
            this.mutate("visitInsn", opcode);
        }
    }
    
    @Override
    public void visitIntInsn(final int opcode, final int operand) {
        this.mutate("visitIntInsn", opcode);
    }
    
    @Override
    public void visitVarInsn(final int opcode, final int var) {
        this.mutate("visitVarInsn", opcode);
    }
    
    @Override
    public void visitTypeInsn(final int opcode, final String type) {
        this.mutate("visitTypeInsn", opcode);
    }
    
    @Override
    public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
        this.mutate("visitFieldInsn", opcode);
    }
    
    @Override
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
        this.mutate("visitMethodInsn", opcode);
    }
    
    @Override
    public void visitInvokeDynamicInsn(final String name, final String desc, final Handle bsm, final Object... bsmArgs) {
        this.mutate("visitInvokeDynamicInsn");
    }
    
    @Override
    public void visitJumpInsn(final int opcode, final Label label) {
        this.mutate("visitJumpInsn", opcode);
    }
    
    @Override
    public void visitLdcInsn(final Object cst) {
        this.mutate("visitLdcInsn");
    }
    
    @Override
    public void visitTableSwitchInsn(final int min, final int max, final Label dflt, final Label... labels) {
        this.mutate("visitTableSwitchInsn");
    }
    
    @Override
    public void visitLookupSwitchInsn(final Label dflt, final int[] keys, final Label[] labels) {
        this.mutate("visitLookupSwitchInsn");
    }
    
    @Override
    public void visitMultiANewArrayInsn(final String desc, final int dims) {
        this.mutate("visitMultiANewArrayInsn");
    }
    
    @Override
    public void visitTryCatchBlock(final Label start, final Label end, final Label handler, final String type) {
        this.mutate("visitTryCatchBlock");
    }
    
    private void mutate(final String string, final int opcode) {
        this.mutate("Null mutation in " + string + " with " + opcode);
    }
    
    private void mutate(final String string) {
        this.context.registerMutation(this.factory, string);
    }
}
