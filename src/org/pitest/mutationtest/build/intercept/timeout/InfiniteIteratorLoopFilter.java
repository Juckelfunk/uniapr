// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build.intercept.timeout;

import org.pitest.sequence.QueryParams;
import org.pitest.sequence.Match;
import org.pitest.sequence.Slot;
import org.objectweb.asm.tree.LabelNode;
import org.pitest.sequence.Context;
import org.pitest.sequence.QueryStart;
import org.pitest.bytecode.analysis.InstructionMatchers;
import org.pitest.classinfo.ClassName;
import java.util.Iterator;
import org.pitest.sequence.SequenceQuery;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.bytecode.analysis.MethodTree;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.pitest.sequence.SequenceMatcher;

public class InfiniteIteratorLoopFilter extends InfiniteLoopFilter
{
    private static final boolean DEBUG = false;
    static final SequenceMatcher<AbstractInsnNode> INFINITE_LOOP;
    
    @Override
    SequenceMatcher<AbstractInsnNode> infiniteLoopMatcher() {
        return InfiniteIteratorLoopFilter.INFINITE_LOOP;
    }
    
    @Override
    boolean couldCauseInfiniteLoop(final MethodTree method, final MutationDetails each) {
        final AbstractInsnNode instruction = method.instructions().get(each.getInstructionIndex());
        return this.isIteratorNext(instruction);
    }
    
    private static SequenceQuery<AbstractInsnNode> doesNotBreakIteratorLoop() {
        return QueryStart.match(InstructionMatchers.methodCallTo(ClassName.fromClass(Iterator.class), "next").negate());
    }
    
    private boolean isIteratorNext(final AbstractInsnNode instruction) {
        return InstructionMatchers.methodCallTo(ClassName.fromClass(Iterator.class), "next").test(null, instruction);
    }
    
    private static SequenceQuery<AbstractInsnNode> inifniteIteratorLoop() {
        final Slot<LabelNode> loopStart = Slot.create(LabelNode.class);
        return QueryStart.any(AbstractInsnNode.class).then(InstructionMatchers.methodCallThatReturns(ClassName.fromString("java/util/Iterator"))).then(InstructionMatchers.opCode(58)).zeroOrMore(QueryStart.match(InstructionMatchers.anyInstruction())).then(InstructionMatchers.aJump()).then(InstructionMatchers.aLabelNode(loopStart.write())).oneOrMore(doesNotBreakIteratorLoop()).then(InstructionMatchers.jumpsTo(loopStart.read())).zeroOrMore(QueryStart.match(InstructionMatchers.jumpsTo(loopStart.read()).negate()));
    }
    
    private static SequenceQuery<AbstractInsnNode> infiniteIteratorLoopJavac() {
        final Slot<LabelNode> loopStart = Slot.create(LabelNode.class);
        return QueryStart.any(AbstractInsnNode.class).then(InstructionMatchers.methodCallThatReturns(ClassName.fromString("java/util/Iterator"))).then(InstructionMatchers.opCode(58)).then(InstructionMatchers.aLabelNode(loopStart.write())).oneOrMore(doesNotBreakIteratorLoop()).then(InstructionMatchers.jumpsTo(loopStart.read())).zeroOrMore(QueryStart.match(InstructionMatchers.jumpsTo(loopStart.read()).negate()));
    }
    
    static {
        INFINITE_LOOP = QueryStart.match(Match.never()).or(inifniteIteratorLoop()).or(infiniteIteratorLoopJavac()).compile(QueryParams.params(AbstractInsnNode.class).withIgnores(InfiniteIteratorLoopFilter.IGNORE).withDebug(false));
    }
}
