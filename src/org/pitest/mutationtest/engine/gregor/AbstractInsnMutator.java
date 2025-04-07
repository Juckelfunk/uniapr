// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor;

import org.pitest.mutationtest.engine.MutationIdentifier;
import java.util.Map;
import org.pitest.reloc.asm.MethodVisitor;

public abstract class AbstractInsnMutator extends MethodVisitor
{
    private final MethodMutatorFactory factory;
    private final MutationContext context;
    private final MethodInfo methodInfo;
    
    public AbstractInsnMutator(final MethodMutatorFactory factory, final MethodInfo methodInfo, final MutationContext context, final MethodVisitor delegateMethodVisitor) {
        super(393216, delegateMethodVisitor);
        this.factory = factory;
        this.methodInfo = methodInfo;
        this.context = context;
    }
    
    protected abstract Map<Integer, ZeroOperandMutation> getMutations();
    
    @Override
    public void visitInsn(final int opcode) {
        if (this.canMutate(opcode)) {
            this.createMutationForInsnOpcode(opcode);
        }
        else {
            this.mv.visitInsn(opcode);
        }
    }
    
    protected boolean canMutate(final int opcode) {
        return this.getMutations().containsKey(opcode);
    }
    
    private void createMutationForInsnOpcode(final int opcode) {
        final ZeroOperandMutation mutation = this.getMutations().get(opcode);
        final MutationIdentifier newId = this.context.registerMutation(this.factory, mutation.decribe(opcode, this.methodInfo));
        if (this.context.shouldMutate(newId)) {
            mutation.apply(opcode, this.mv);
        }
        else {
            this.applyUnmutatedInstruction(opcode);
        }
    }
    
    private void applyUnmutatedInstruction(final int opcode) {
        this.mv.visitInsn(opcode);
    }
    
    protected MethodInfo methodInfo() {
        return this.methodInfo;
    }
}
