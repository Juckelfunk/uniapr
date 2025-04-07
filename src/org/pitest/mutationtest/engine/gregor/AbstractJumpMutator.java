// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor;

import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.reloc.asm.Label;
import java.util.Map;
import org.pitest.reloc.asm.MethodVisitor;

public abstract class AbstractJumpMutator extends MethodVisitor
{
    private final MethodMutatorFactory factory;
    private final MutationContext context;
    
    public AbstractJumpMutator(final MethodMutatorFactory factory, final MutationContext context, final MethodVisitor writer) {
        super(393216, writer);
        this.factory = factory;
        this.context = context;
    }
    
    protected abstract Map<Integer, Substitution> getMutations();
    
    @Override
    public void visitJumpInsn(final int opcode, final Label label) {
        if (this.canMutate(opcode)) {
            this.createMutationForJumpInsn(opcode, label);
        }
        else {
            this.mv.visitJumpInsn(opcode, label);
        }
    }
    
    private boolean canMutate(final int opcode) {
        return this.getMutations().containsKey(opcode);
    }
    
    private void createMutationForJumpInsn(final int opcode, final Label label) {
        final Substitution substitution = this.getMutations().get(opcode);
        final MutationIdentifier newId = this.context.registerMutation(this.factory, substitution.description);
        if (this.context.shouldMutate(newId)) {
            this.mv.visitJumpInsn(substitution.newCode, label);
        }
        else {
            this.mv.visitJumpInsn(opcode, label);
        }
    }
    
    public static class Substitution
    {
        private final int newCode;
        private final String description;
        
        public Substitution(final int newCode, final String description) {
            this.newCode = newCode;
            this.description = description;
        }
    }
}
