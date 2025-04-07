// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build.intercept.timeout;

import org.pitest.sequence.QueryParams;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.pitest.bytecode.analysis.InstructionMatchers;
import org.pitest.sequence.QueryStart;
import org.objectweb.asm.tree.LabelNode;
import org.pitest.sequence.Slot;
import org.pitest.sequence.SequenceQuery;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.bytecode.analysis.MethodTree;
import org.pitest.sequence.SequenceMatcher;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.pitest.sequence.Match;

public class InfiniteForLoopFilter extends InfiniteLoopFilter
{
    private static final boolean DEBUG = false;
    private static final Match<AbstractInsnNode> IGNORE;
    static final SequenceMatcher<AbstractInsnNode> INFINITE_LOOP;
    
    @Override
    SequenceMatcher<AbstractInsnNode> infiniteLoopMatcher() {
        return InfiniteForLoopFilter.INFINITE_LOOP;
    }
    
    @Override
    boolean couldCauseInfiniteLoop(final MethodTree method, final MutationDetails each) {
        final AbstractInsnNode instruction = method.instructions().get(each.getInstructionIndex());
        return instruction.getOpcode() == 132;
    }
    
    private static SequenceQuery<AbstractInsnNode> countingLoopWithoutWriteConditionalAtStart() {
        final Slot<Integer> counterVariable = Slot.create(Integer.class);
        final Slot<LabelNode> loopStart = Slot.create(LabelNode.class);
        return QueryStart.any(AbstractInsnNode.class).then(InstructionMatchers.anIntegerConstant().and(InstructionMatchers.debug("constant"))).zeroOrMore(QueryStart.match(InstructionMatchers.opCode(96))).then(InstructionMatchers.anIStore(counterVariable.write()).and(InstructionMatchers.debug("counter"))).zeroOrMore(QueryStart.match(InstructionMatchers.opCode(21).or(InstructionMatchers.opCode(25).or(InstructionMatchers.opCode(54)).or(InstructionMatchers.methodCall())))).then(InstructionMatchers.aLabelNode(loopStart.write()).and(InstructionMatchers.debug("label"))).then(InstructionMatchers.anILoadOf(counterVariable.read()).and(InstructionMatchers.debug("load"))).zeroOrMore(doesNotBreakLoop(counterVariable)).zeroOrMore(QueryStart.match(InstructionMatchers.opCode(21).or(InstructionMatchers.opCode(25).or(InstructionMatchers.methodCall())))).then(InstructionMatchers.aConditionalJump().and(InstructionMatchers.debug("jump"))).zeroOrMore(doesNotBreakLoop(counterVariable)).then(InstructionMatchers.jumpsTo(loopStart.read())).zeroOrMore(QueryStart.match(InstructionMatchers.anyInstruction()));
    }
    
    private static SequenceQuery<AbstractInsnNode> countingLoopWithoutWriteConditionAtEnd() {
        final Slot<Integer> counterVariable = Slot.create(Integer.class);
        final Slot<LabelNode> loopStart = Slot.create(LabelNode.class);
        final Slot<LabelNode> loopEnd = Slot.create(LabelNode.class);
        return QueryStart.any(AbstractInsnNode.class).then(InstructionMatchers.anIntegerConstant()).then(InstructionMatchers.anIStore(counterVariable.write()).and(InstructionMatchers.debug("counter"))).then(InstructionMatchers.isA(LabelNode.class)).then(InstructionMatchers.gotoLabel(loopEnd.write())).then(InstructionMatchers.aLabelNode(loopStart.write()).and(InstructionMatchers.debug("loop start"))).zeroOrMore(doesNotBreakLoop(counterVariable)).then(InstructionMatchers.labelNode(loopEnd.read()).and(InstructionMatchers.debug("loop end"))).then(InstructionMatchers.anILoadOf(counterVariable.read()).and(InstructionMatchers.debug("read"))).zeroOrMore(doesNotBreakLoop(counterVariable)).then(InstructionMatchers.jumpsTo(loopStart.read()).and(InstructionMatchers.debug("jump"))).zeroOrMore(QueryStart.match(InstructionMatchers.anyInstruction()));
    }
    
    private static SequenceQuery<AbstractInsnNode> doesNotBreakLoop(final Slot<Integer> counterVariable) {
        return QueryStart.match(InstructionMatchers.anIStoreTo(counterVariable.read()).and(InstructionMatchers.debug("broken by store")).or(InstructionMatchers.incrementsVariable(counterVariable.read())).negate());
    }
    
    static {
        IGNORE = InstructionMatchers.isA(LineNumberNode.class).or(InstructionMatchers.isA(FrameNode.class));
        INFINITE_LOOP = QueryStart.match(Match.never()).or(countingLoopWithoutWriteConditionalAtStart()).or(countingLoopWithoutWriteConditionAtEnd()).compile(QueryParams.params(AbstractInsnNode.class).withIgnores(InfiniteForLoopFilter.IGNORE).withDebug(false));
    }
}
