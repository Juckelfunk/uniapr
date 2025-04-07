// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build.intercept.equivalent;

import org.pitest.mutationtest.engine.gregor.mutators.NullReturnValsMutator;
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
import org.pitest.mutationtest.build.MutationInterceptor;

class NullReturnsFilter implements MutationInterceptor
{
    private static final String MUTATOR_ID;
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
                if (!NullReturnsFilter.MUTATOR_ID.equals(a.getMutator())) {
                    return false;
                }
                final MethodTree method = NullReturnsFilter.this.currentClass.methods().findFirst(MethodMatchers.forLocation(a.getId().getLocation())).value();
                final int mutatedInstruction = a.getInstructionIndex();
                return this.returnsNull(method, mutatedInstruction);
            }
            
            private Boolean returnsNull(final MethodTree method, final int mutatedInstruction) {
                return method.instructions().get(mutatedInstruction - 1).getOpcode() == 1;
            }
        };
    }
    
    @Override
    public void end() {
        this.currentClass = null;
    }
    
    static {
        MUTATOR_ID = NullReturnValsMutator.NULL_RETURN_VALUES.getGloballyUniqueId();
    }
}
