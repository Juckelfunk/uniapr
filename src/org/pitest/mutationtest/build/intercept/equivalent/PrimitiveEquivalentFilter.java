// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build.intercept.equivalent;

import org.pitest.mutationtest.engine.gregor.mutators.BooleanFalseReturnValsMutator;
import org.pitest.mutationtest.engine.gregor.mutators.PrimitiveReturnsMutator;
import java.util.HashSet;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.pitest.bytecode.analysis.MethodMatchers;
import org.pitest.bytecode.analysis.MethodTree;
import org.pitest.functional.F;
import org.pitest.functional.FCollection;
import org.pitest.functional.prelude.Prelude;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;
import java.util.Collection;
import org.pitest.mutationtest.build.InterceptorType;
import org.pitest.bytecode.analysis.ClassTree;
import java.util.Set;
import org.pitest.mutationtest.build.MutationInterceptor;

class PrimitiveEquivalentFilter implements MutationInterceptor
{
    private static final Set<String> MUTATOR_IDS;
    private static final Set<Integer> ZERO_CONSTANTS;
    private ClassTree currentClass;
    
    @Override
    public InterceptorType type() {
        return InterceptorType.FILTER;
    }
    
    @Override
    public void begin(final ClassTree clazz) {
        this.currentClass = clazz;
    }
    
    @Override
    public Collection<MutationDetails> intercept(final Collection<MutationDetails> mutations, final Mutater m) {
        return (Collection<MutationDetails>)FCollection.filter(mutations, (F<Object, Boolean>)Prelude.not(this.isEquivalent(m)));
    }
    
    private F<MutationDetails, Boolean> isEquivalent(final Mutater m) {
        return new F<MutationDetails, Boolean>() {
            @Override
            public Boolean apply(final MutationDetails a) {
                if (!PrimitiveEquivalentFilter.MUTATOR_IDS.contains(a.getMutator())) {
                    return false;
                }
                final int intructionBeforeReturn = a.getInstructionIndex() - 1;
                final MethodTree method = PrimitiveEquivalentFilter.this.currentClass.methods().findFirst(MethodMatchers.forLocation(a.getId().getLocation())).value();
                return PrimitiveEquivalentFilter.ZERO_CONSTANTS.contains(method.instructions().get(intructionBeforeReturn).getOpcode());
            }
        };
    }
    
    @Override
    public void end() {
        this.currentClass = null;
    }
    
    static {
        MUTATOR_IDS = new HashSet<String>();
        (ZERO_CONSTANTS = new HashSet<Integer>()).add(3);
        PrimitiveEquivalentFilter.ZERO_CONSTANTS.add(9);
        PrimitiveEquivalentFilter.ZERO_CONSTANTS.add(11);
        PrimitiveEquivalentFilter.ZERO_CONSTANTS.add(14);
        PrimitiveEquivalentFilter.MUTATOR_IDS.add(PrimitiveReturnsMutator.PRIMITIVE_RETURN_VALS_MUTATOR.getGloballyUniqueId());
        PrimitiveEquivalentFilter.MUTATOR_IDS.add(BooleanFalseReturnValsMutator.BOOLEAN_FALSE_RETURN.getGloballyUniqueId());
    }
}
