// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build.intercept.equivalent;

import org.pitest.mutationtest.engine.gregor.mutators.BooleanTrueReturnValsMutator;
import java.util.HashSet;
import org.objectweb.asm.tree.MethodInsnNode;
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

class HardCodedTrueEquivalentFilter implements MutationInterceptor
{
    private static final Set<String> MUTATOR_IDS;
    private static final Set<Integer> TRUE_CONSTANTS;
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
                if (!HardCodedTrueEquivalentFilter.MUTATOR_IDS.contains(a.getMutator())) {
                    return false;
                }
                final int instruction = a.getInstructionIndex();
                final MethodTree method = HardCodedTrueEquivalentFilter.this.currentClass.methods().findFirst(MethodMatchers.forLocation(a.getId().getLocation())).value();
                return this.primitiveTrue(instruction, method) || this.boxedTrue(instruction, method);
            }
            
            private boolean primitiveTrue(final int instruction, final MethodTree method) {
                return method.instructions().get(instruction - 1).getOpcode() == 4;
            }
            
            private boolean boxedTrue(final int instruction, final MethodTree method) {
                return method.instructions().get(instruction - 2).getOpcode() == 4 && HardCodedTrueEquivalentFilter.this.isValueOfCall(instruction - 1, method);
            }
        };
    }
    
    @Override
    public void end() {
        this.currentClass = null;
    }
    
    private boolean isValueOfCall(final int instruction, final MethodTree method) {
        final AbstractInsnNode abstractInsnNode = method.instructions().get(instruction);
        return abstractInsnNode instanceof MethodInsnNode && ((MethodInsnNode)abstractInsnNode).name.equals("valueOf");
    }
    
    static {
        MUTATOR_IDS = new HashSet<String>();
        (TRUE_CONSTANTS = new HashSet<Integer>()).add(4);
        HardCodedTrueEquivalentFilter.MUTATOR_IDS.add(BooleanTrueReturnValsMutator.BOOLEAN_TRUE_RETURN.getGloballyUniqueId());
    }
}
