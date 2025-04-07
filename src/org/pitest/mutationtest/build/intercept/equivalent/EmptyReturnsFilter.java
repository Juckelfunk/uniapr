// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build.intercept.equivalent;

import org.pitest.mutationtest.engine.gregor.mutators.BooleanFalseReturnValsMutator;
import org.pitest.mutationtest.engine.gregor.mutators.EmptyObjectReturnValsMutator;
import java.util.HashSet;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.Type;
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

class EmptyReturnsFilter implements MutationInterceptor
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
                if (!EmptyReturnsFilter.MUTATOR_IDS.contains(a.getMutator())) {
                    return false;
                }
                final MethodTree method = EmptyReturnsFilter.this.currentClass.methods().findFirst(MethodMatchers.forLocation(a.getId().getLocation())).value();
                final int mutatedInstruction = a.getInstructionIndex();
                return this.returnsZeroValue(method, mutatedInstruction) || this.returnsEmptyString(method, mutatedInstruction) || this.returns(method, mutatedInstruction, "java/util/Optional", "empty") || this.returns(method, mutatedInstruction, "java/util/Collections", "emptyList") || this.returns(method, mutatedInstruction, "java/util/Collections", "emptySet") || this.returns(method, mutatedInstruction, "java/util/List", "of") || this.returns(method, mutatedInstruction, "java/util/Set", "of");
            }
            
            private Boolean returnsZeroValue(final MethodTree method, final int mutatedInstruction) {
                return this.isValueOf(method.instructions().get(mutatedInstruction - 1)) && EmptyReturnsFilter.ZERO_CONSTANTS.contains(method.instructions().get(mutatedInstruction - 2).getOpcode());
            }
            
            private boolean isValueOf(final AbstractInsnNode abstractInsnNode) {
                return abstractInsnNode instanceof MethodInsnNode && ((MethodInsnNode)abstractInsnNode).name.equals("valueOf");
            }
            
            private boolean returns(final MethodTree method, final int mutatedInstruction, final String owner, final String name) {
                final AbstractInsnNode node = method.instructions().get(mutatedInstruction - 1);
                if (node instanceof MethodInsnNode) {
                    final MethodInsnNode call = (MethodInsnNode)node;
                    return call.owner.equals(owner) && call.name.equals(name) && this.takesNoArguments(call.desc);
                }
                return false;
            }
            
            private boolean takesNoArguments(final String desc) {
                return Type.getArgumentTypes(desc).length == 0;
            }
            
            private boolean returnsEmptyString(final MethodTree method, final int mutatedInstruction) {
                final AbstractInsnNode node = method.instructions().get(mutatedInstruction - 1);
                if (node instanceof LdcInsnNode) {
                    final LdcInsnNode ldc = (LdcInsnNode)node;
                    return "".equals(ldc.cst);
                }
                return false;
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
        EmptyReturnsFilter.ZERO_CONSTANTS.add(9);
        EmptyReturnsFilter.ZERO_CONSTANTS.add(11);
        EmptyReturnsFilter.ZERO_CONSTANTS.add(14);
        EmptyReturnsFilter.MUTATOR_IDS.add(EmptyObjectReturnValsMutator.EMPTY_RETURN_VALUES.getGloballyUniqueId());
        EmptyReturnsFilter.MUTATOR_IDS.add(BooleanFalseReturnValsMutator.BOOLEAN_FALSE_RETURN.getGloballyUniqueId());
    }
}
