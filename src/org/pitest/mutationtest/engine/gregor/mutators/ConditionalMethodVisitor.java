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

class ConditionalMethodVisitor extends AbstractJumpMutator
{
    private static final String DESCRIPTION = "negated conditional";
    private static final Map<Integer, Substitution> MUTATIONS;
    
    ConditionalMethodVisitor(final MethodMutatorFactory factory, final MutationContext context, final MethodVisitor delegateMethodVisitor) {
        super(factory, context, delegateMethodVisitor);
    }
    
    @Override
    protected Map<Integer, Substitution> getMutations() {
        return ConditionalMethodVisitor.MUTATIONS;
    }
    
    static {
        (MUTATIONS = new HashMap<Integer, Substitution>()).put(153, new Substitution(154, "negated conditional"));
        ConditionalMethodVisitor.MUTATIONS.put(154, new Substitution(153, "negated conditional"));
        ConditionalMethodVisitor.MUTATIONS.put(158, new Substitution(157, "negated conditional"));
        ConditionalMethodVisitor.MUTATIONS.put(156, new Substitution(155, "negated conditional"));
        ConditionalMethodVisitor.MUTATIONS.put(157, new Substitution(158, "negated conditional"));
        ConditionalMethodVisitor.MUTATIONS.put(155, new Substitution(156, "negated conditional"));
        ConditionalMethodVisitor.MUTATIONS.put(198, new Substitution(199, "negated conditional"));
        ConditionalMethodVisitor.MUTATIONS.put(199, new Substitution(198, "negated conditional"));
        ConditionalMethodVisitor.MUTATIONS.put(160, new Substitution(159, "negated conditional"));
        ConditionalMethodVisitor.MUTATIONS.put(159, new Substitution(160, "negated conditional"));
        ConditionalMethodVisitor.MUTATIONS.put(164, new Substitution(163, "negated conditional"));
        ConditionalMethodVisitor.MUTATIONS.put(162, new Substitution(161, "negated conditional"));
        ConditionalMethodVisitor.MUTATIONS.put(163, new Substitution(164, "negated conditional"));
        ConditionalMethodVisitor.MUTATIONS.put(161, new Substitution(162, "negated conditional"));
        ConditionalMethodVisitor.MUTATIONS.put(165, new Substitution(166, "negated conditional"));
        ConditionalMethodVisitor.MUTATIONS.put(166, new Substitution(165, "negated conditional"));
    }
}
