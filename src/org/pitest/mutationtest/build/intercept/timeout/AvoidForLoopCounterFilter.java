// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build.intercept.timeout;

import org.pitest.sequence.QueryParams;
import org.objectweb.asm.tree.FrameNode;
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
import org.objectweb.asm.tree.MethodInsnNode;
import org.pitest.bytecode.analysis.InstructionMatchers;
import org.pitest.sequence.QueryStart;
import org.objectweb.asm.tree.LabelNode;
import org.pitest.sequence.SequenceQuery;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.sequence.SequenceMatcher;
import org.pitest.sequence.Slot;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.pitest.sequence.Match;
import org.pitest.mutationtest.build.MutationInterceptor;

public class AvoidForLoopCounterFilter implements MutationInterceptor
{
    private static final boolean DEBUG = false;
    private static final Match<AbstractInsnNode> IGNORE;
    private static final Slot<AbstractInsnNode> MUTATED_INSTRUCTION;
    static final SequenceMatcher<AbstractInsnNode> MUTATED_FOR_COUNTER;
    private ClassTree currentClass;
    
    private static SequenceQuery<AbstractInsnNode> conditionalAtEnd() {
        final Slot<Integer> counterVariable = Slot.create(Integer.class);
        final Slot<LabelNode> loopStart = Slot.create(LabelNode.class);
        final Slot<LabelNode> loopEnd = Slot.create(LabelNode.class);
        return QueryStart.any(AbstractInsnNode.class).then(InstructionMatchers.anIStore(counterVariable.write()).and(InstructionMatchers.debug("end_counter"))).then(InstructionMatchers.isA(LabelNode.class)).then(InstructionMatchers.gotoLabel(loopEnd.write())).then(InstructionMatchers.aLabelNode(loopStart.write()).and(InstructionMatchers.debug("loop start"))).zeroOrMore(anything()).then(targetInstruction(counterVariable).and(InstructionMatchers.debug("target"))).then(InstructionMatchers.labelNode(loopEnd.read()).and(InstructionMatchers.debug("loop end"))).then(InstructionMatchers.anILoadOf(counterVariable.read()).and(InstructionMatchers.debug("read"))).zeroOrMore(anything()).then(loadsAnIntegerToCompareTo()).then(InstructionMatchers.aConditionalJumpTo(loopStart).and(InstructionMatchers.debug("jump"))).zeroOrMore(anything());
    }
    
    private static SequenceQuery<AbstractInsnNode> conditionalAtStart() {
        final Slot<Integer> counterVariable = Slot.create(Integer.class);
        final Slot<LabelNode> loopStart = Slot.create(LabelNode.class);
        final Slot<LabelNode> loopEnd = Slot.create(LabelNode.class);
        return QueryStart.any(AbstractInsnNode.class).then(InstructionMatchers.anIStore(counterVariable.write()).and(InstructionMatchers.debug("store"))).then(InstructionMatchers.aLabelNode(loopStart.write()).and(InstructionMatchers.debug("label"))).then(InstructionMatchers.anILoadOf(counterVariable.read()).and(InstructionMatchers.debug("load"))).zeroOrMore(QueryStart.match(InstructionMatchers.opCode(25))).then(loadsAnIntegerToCompareTo().and(InstructionMatchers.debug("push"))).then(InstructionMatchers.jumpsTo(loopEnd.write()).and(InstructionMatchers.aConditionalJump())).then(InstructionMatchers.isA(LabelNode.class)).zeroOrMore(anything()).then(targetInstruction(counterVariable).and(InstructionMatchers.debug("target"))).then(InstructionMatchers.jumpsTo(loopStart.read()).and(InstructionMatchers.debug("jump"))).then(InstructionMatchers.labelNode(loopEnd.read())).zeroOrMore(anything());
    }
    
    private static Match<AbstractInsnNode> loadsAnIntegerToCompareTo() {
        return InstructionMatchers.opCode(16).or(integerMethodCall()).or(arrayLength());
    }
    
    private static Match<AbstractInsnNode> arrayLength() {
        return InstructionMatchers.opCode(190);
    }
    
    private static SequenceQuery<AbstractInsnNode> anything() {
        return QueryStart.match(InstructionMatchers.anyInstruction());
    }
    
    private static Match<AbstractInsnNode> integerMethodCall() {
        return InstructionMatchers.isA(MethodInsnNode.class);
    }
    
    private static Match<AbstractInsnNode> targetInstruction(final Slot<Integer> counterVariable) {
        return InstructionMatchers.incrementsVariable(counterVariable.read()).and(InstructionMatchers.isInstruction(AvoidForLoopCounterFilter.MUTATED_INSTRUCTION.read()));
    }
    
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
        return (Collection<MutationDetails>)FCollection.filter(mutations, (F<Object, Boolean>)Prelude.not(this.mutatesAForLoopCounter()));
    }
    
    private F<MutationDetails, Boolean> mutatesAForLoopCounter() {
        return new F<MutationDetails, Boolean>() {
            @Override
            public Boolean apply(final MutationDetails a) {
                final int instruction = a.getInstructionIndex();
                final MethodTree method = AvoidForLoopCounterFilter.this.currentClass.methods().findFirst(MethodMatchers.forLocation(a.getId().getLocation())).value();
                final AbstractInsnNode mutatedInstruction = method.instructions().get(instruction);
                final Context<AbstractInsnNode> context = Context.start(method.instructions(), false);
                context.store(AvoidForLoopCounterFilter.MUTATED_INSTRUCTION.write(), mutatedInstruction);
                return AvoidForLoopCounterFilter.MUTATED_FOR_COUNTER.matches(method.instructions(), context);
            }
        };
    }
    
    @Override
    public void end() {
        this.currentClass = null;
    }
    
    static {
        IGNORE = InstructionMatchers.isA(LineNumberNode.class).or(InstructionMatchers.isA(FrameNode.class).or(InstructionMatchers.opCode(180)));
        MUTATED_INSTRUCTION = Slot.create(AbstractInsnNode.class);
        MUTATED_FOR_COUNTER = QueryStart.match(Match.never()).or(conditionalAtEnd()).or(conditionalAtStart()).compile(QueryParams.params(AbstractInsnNode.class).withIgnores(AvoidForLoopCounterFilter.IGNORE).withDebug(false));
    }
}
