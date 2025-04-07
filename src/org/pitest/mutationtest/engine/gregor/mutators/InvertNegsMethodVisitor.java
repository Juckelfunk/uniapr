// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.mutators;

import org.pitest.mutationtest.engine.gregor.InsnSubstitution;
import java.util.HashMap;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import org.pitest.mutationtest.engine.gregor.ZeroOperandMutation;
import java.util.Map;
import org.pitest.mutationtest.engine.gregor.AbstractInsnMutator;

class InvertNegsMethodVisitor extends AbstractInsnMutator
{
    private static final String MESSAGE = "removed negation";
    private static final Map<Integer, ZeroOperandMutation> MUTATIONS;
    
    InvertNegsMethodVisitor(final MethodMutatorFactory factory, final MethodInfo methodInfo, final MutationContext context, final MethodVisitor writer) {
        super(factory, methodInfo, context, writer);
    }
    
    @Override
    protected Map<Integer, ZeroOperandMutation> getMutations() {
        return InvertNegsMethodVisitor.MUTATIONS;
    }
    
    static {
        (MUTATIONS = new HashMap<Integer, ZeroOperandMutation>()).put(116, new InsnSubstitution(0, "removed negation"));
        InvertNegsMethodVisitor.MUTATIONS.put(119, new InsnSubstitution(0, "removed negation"));
        InvertNegsMethodVisitor.MUTATIONS.put(118, new InsnSubstitution(0, "removed negation"));
        InvertNegsMethodVisitor.MUTATIONS.put(117, new InsnSubstitution(0, "removed negation"));
    }
}
