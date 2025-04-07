// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build.intercept.javafeatures;

import org.pitest.sequence.QueryParams;
import org.objectweb.asm.tree.LabelNode;
import org.pitest.classinfo.ClassName;
import org.pitest.sequence.QueryStart;
import org.objectweb.asm.tree.FrameNode;
import org.pitest.bytecode.analysis.InstructionMatchers;
import org.objectweb.asm.tree.LineNumberNode;
import java.util.List;
import org.pitest.sequence.Context;
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
import org.pitest.sequence.SequenceMatcher;
import org.pitest.sequence.Slot;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.pitest.sequence.Match;
import org.pitest.mutationtest.build.MutationInterceptor;

public class ImplicitNullCheckFilter implements MutationInterceptor
{
    private static final boolean DEBUG = false;
    private static final Match<AbstractInsnNode> IGNORE;
    private static final Slot<AbstractInsnNode> MUTATED_INSTRUCTION;
    static final SequenceMatcher<AbstractInsnNode> GET_CLASS_NULL_CHECK;
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
        return (Collection<MutationDetails>)FCollection.filter(mutations, (F<Object, Boolean>)Prelude.not(this.isAnImplicitNullCheck()));
    }
    
    private F<MutationDetails, Boolean> isAnImplicitNullCheck() {
        return new F<MutationDetails, Boolean>() {
            @Override
            public Boolean apply(final MutationDetails a) {
                final int instruction = a.getInstructionIndex();
                final MethodTree method = ImplicitNullCheckFilter.this.currentClass.methods().findFirst(MethodMatchers.forLocation(a.getId().getLocation())).value();
                final AbstractInsnNode mutatedInstruction = method.instructions().get(instruction);
                final Context<AbstractInsnNode> context = Context.start(method.instructions(), false);
                context.store(ImplicitNullCheckFilter.MUTATED_INSTRUCTION.write(), mutatedInstruction);
                return ImplicitNullCheckFilter.GET_CLASS_NULL_CHECK.matches(method.instructions(), context);
            }
        };
    }
    
    @Override
    public void end() {
        this.currentClass = null;
    }
    
    static {
        IGNORE = InstructionMatchers.isA(LineNumberNode.class).or(InstructionMatchers.isA(FrameNode.class));
        MUTATED_INSTRUCTION = Slot.create(AbstractInsnNode.class);
        GET_CLASS_NULL_CHECK = QueryStart.any(AbstractInsnNode.class).then(InstructionMatchers.methodCallTo(ClassName.fromClass(Object.class), "getClass").and(InstructionMatchers.isInstruction(ImplicitNullCheckFilter.MUTATED_INSTRUCTION.read()))).then(InstructionMatchers.opCode(87)).then(InstructionMatchers.isA(LabelNode.class).negate()).zeroOrMore(QueryStart.match(InstructionMatchers.anyInstruction())).compile(QueryParams.params(AbstractInsnNode.class).withIgnores(ImplicitNullCheckFilter.IGNORE).withDebug(false));
    }
}
