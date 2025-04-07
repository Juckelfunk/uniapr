// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.mutators.experimental;

import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import org.pitest.reloc.asm.MethodVisitor;

class RemoveIncrementsMethodVisitor extends MethodVisitor
{
    private final MethodMutatorFactory factory;
    private final MutationContext context;
    
    RemoveIncrementsMethodVisitor(final MethodMutatorFactory factory, final MutationContext context, final MethodVisitor delegateMethodVisitor) {
        super(393216, delegateMethodVisitor);
        this.factory = factory;
        this.context = context;
    }
    
    @Override
    public void visitIincInsn(final int var, final int increment) {
        final MutationIdentifier newId = this.context.registerMutation(this.factory, "Removed increment " + increment);
        if (this.context.shouldMutate(newId)) {
            this.mv.visitInsn(0);
        }
        else {
            this.mv.visitIincInsn(var, increment);
        }
    }
}
