// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest;

import org.pitest.mutationtest.engine.gregor.InsnSubstitution;
import java.util.HashMap;
import org.pitest.mutationtest.engine.gregor.ZeroOperandMutation;
import java.util.Map;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import org.pitest.mutationtest.engine.gregor.AbstractInsnMutator;

class UnviableClassMethodVisitor extends AbstractInsnMutator
{
    UnviableClassMethodVisitor(final MethodMutatorFactory factory, final MethodInfo methodInfo, final MutationContext context, final MethodVisitor writer) {
        super(factory, methodInfo, context, writer);
    }
    
    @Override
    protected Map<Integer, ZeroOperandMutation> getMutations() {
        final Map<Integer, ZeroOperandMutation> map = new HashMap<Integer, ZeroOperandMutation>();
        map.put(172, new InsnSubstitution(150, "Made unviable class"));
        map.put(177, new InsnSubstitution(150, "Made unviable class"));
        return map;
    }
}
