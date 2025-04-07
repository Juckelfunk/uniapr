// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.mutators;

import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import org.pitest.reloc.asm.MethodVisitor;

class IncrementsMethodVisitor extends MethodVisitor
{
    private final MethodMutatorFactory factory;
    private final MutationContext context;
    
    IncrementsMethodVisitor(final MethodMutatorFactory factory, final MutationContext context, final MethodVisitor delegateMethodVisitor) {
        super(393216, delegateMethodVisitor);
        this.factory = factory;
        this.context = context;
    }
    
    @Override
    public void visitIincInsn(final int var, final int increment) {
        final MutationIdentifier newId = this.context.registerMutation(this.factory, "Changed increment from " + increment + " to " + -increment);
        if (this.context.shouldMutate(newId)) {
            this.mv.visitIincInsn(var, -increment);
        }
        else {
            this.mv.visitIincInsn(var, increment);
        }
    }
}
