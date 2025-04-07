// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.mutators;

import java.util.HashMap;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import java.util.Map;
import org.pitest.mutationtest.engine.gregor.AbstractJumpMutator;

class ConditionalsBoundaryMethodVisitor extends AbstractJumpMutator
{
    private static final String DESCRIPTION = "changed conditional boundary";
    private static final Map<Integer, Substitution> MUTATIONS;
    
    ConditionalsBoundaryMethodVisitor(final MethodMutatorFactory factory, final MutationContext context, final MethodVisitor delegateMethodVisitor) {
        super(factory, context, delegateMethodVisitor);
    }
    
    @Override
    protected Map<Integer, Substitution> getMutations() {
        return ConditionalsBoundaryMethodVisitor.MUTATIONS;
    }
    
    static {
        (MUTATIONS = new HashMap<Integer, Substitution>()).put(158, new Substitution(155, "changed conditional boundary"));
        ConditionalsBoundaryMethodVisitor.MUTATIONS.put(156, new Substitution(157, "changed conditional boundary"));
        ConditionalsBoundaryMethodVisitor.MUTATIONS.put(157, new Substitution(156, "changed conditional boundary"));
        ConditionalsBoundaryMethodVisitor.MUTATIONS.put(155, new Substitution(158, "changed conditional boundary"));
        ConditionalsBoundaryMethodVisitor.MUTATIONS.put(164, new Substitution(161, "changed conditional boundary"));
        ConditionalsBoundaryMethodVisitor.MUTATIONS.put(162, new Substitution(163, "changed conditional boundary"));
        ConditionalsBoundaryMethodVisitor.MUTATIONS.put(163, new Substitution(162, "changed conditional boundary"));
        ConditionalsBoundaryMethodVisitor.MUTATIONS.put(161, new Substitution(164, "changed conditional boundary"));
    }
}
