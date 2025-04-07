// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build.intercept.javafeatures;

import org.pitest.sequence.QueryParams;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.LineNumberNode;
import java.util.List;
import org.pitest.bytecode.analysis.MethodMatchers;
import org.pitest.bytecode.analysis.MethodTree;
import org.pitest.functional.F;
import org.pitest.functional.FCollection;
import org.pitest.functional.prelude.Prelude;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;
import java.util.Collection;
import org.pitest.mutationtest.build.InterceptorType;
import org.pitest.sequence.Context;
import java.util.Iterator;
import org.pitest.classinfo.ClassName;
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

public class ForEachLoopFilter implements MutationInterceptor
{
    private static final boolean DEBUG = false;
    private static final Match<AbstractInsnNode> IGNORE;
    private static final Slot<AbstractInsnNode> MUTATED_INSTRUCTION;
    private static final Slot<Boolean> FOUND;
    private static final SequenceMatcher<AbstractInsnNode> ITERATOR_LOOP;
    private ClassTree currentClass;
    
    private static SequenceQuery<AbstractInsnNode> conditionalAtEnd() {
        final Slot<LabelNode> loopStart = Slot.create(LabelNode.class);
        final Slot<LabelNode> loopEnd = Slot.create(LabelNode.class);
        return QueryStart.any(AbstractInsnNode.class).zeroOrMore(QueryStart.match(InstructionMatchers.anyInstruction())).then(aMethodCallReturningAnIterator().and(mutationPoint())).then(InstructionMatchers.opCode(58)).then(InstructionMatchers.gotoLabel(loopEnd.write())).then(InstructionMatchers.aLabelNode(loopStart.write())).then(InstructionMatchers.opCode(25)).then(InstructionMatchers.methodCallTo(ClassName.fromString("java/util/Iterator"), "next").and(mutationPoint())).zeroOrMore(QueryStart.match(InstructionMatchers.anyInstruction())).then(InstructionMatchers.labelNode(loopEnd.read())).then(InstructionMatchers.opCode(25)).then(InstructionMatchers.methodCallTo(ClassName.fromString("java/util/Iterator"), "hasNext").and(mutationPoint())).then(InstructionMatchers.aConditionalJumpTo(loopStart).and(mutationPoint())).zeroOrMore(QueryStart.match(InstructionMatchers.anyInstruction()));
    }
    
    private static SequenceQuery<AbstractInsnNode> conditionalAtStart() {
        final Slot<LabelNode> loopStart = Slot.create(LabelNode.class);
        final Slot<LabelNode> loopEnd = Slot.create(LabelNode.class);
        return QueryStart.any(AbstractInsnNode.class).zeroOrMore(QueryStart.match(InstructionMatchers.anyInstruction())).then(aMethodCallReturningAnIterator().and(mutationPoint())).then(InstructionMatchers.opCode(58)).then(InstructionMatchers.aLabelNode(loopStart.write())).then(InstructionMatchers.opCode(25)).then(InstructionMatchers.methodCallTo(ClassName.fromString("java/util/Iterator"), "hasNext").and(mutationPoint())).then(InstructionMatchers.aConditionalJump().and(InstructionMatchers.jumpsTo(loopEnd.write())).and(mutationPoint())).then(InstructionMatchers.opCode(25)).then(InstructionMatchers.methodCallTo(ClassName.fromString("java/util/Iterator"), "next").and(mutationPoint())).zeroOrMore(QueryStart.match(InstructionMatchers.anyInstruction())).then(InstructionMatchers.opCode(167).and(InstructionMatchers.jumpsTo(loopStart.read()))).then(InstructionMatchers.labelNode(loopEnd.read())).zeroOrMore(QueryStart.match(InstructionMatchers.anyInstruction()));
    }
    
    private static SequenceQuery<AbstractInsnNode> arrayConditionalAtEnd() {
        final Slot<LabelNode> loopStart = Slot.create(LabelNode.class);
        final Slot<LabelNode> loopEnd = Slot.create(LabelNode.class);
        final Slot<Integer> counter = Slot.create(Integer.class);
        return QueryStart.any(AbstractInsnNode.class).zeroOrMore(QueryStart.match(InstructionMatchers.anyInstruction())).then(InstructionMatchers.opCode(190).and(mutationPoint())).then(InstructionMatchers.opCode(54)).then(InstructionMatchers.opCode(3).and(mutationPoint())).then(InstructionMatchers.anIStore(counter.write()).and(InstructionMatchers.debug("store"))).then(InstructionMatchers.gotoLabel(loopEnd.write())).then(InstructionMatchers.aLabelNode(loopStart.write())).zeroOrMore(QueryStart.match(InstructionMatchers.anyInstruction())).then(InstructionMatchers.incrementsVariable(counter.read()).and(mutationPoint())).then(InstructionMatchers.labelNode(loopEnd.read())).then(InstructionMatchers.opCode(21)).then(InstructionMatchers.opCode(21)).then(InstructionMatchers.aConditionalJumpTo(loopStart).and(mutationPoint())).zeroOrMore(QueryStart.match(InstructionMatchers.anyInstruction()));
    }
    
    private static SequenceQuery<AbstractInsnNode> arrayConditionalAtStart() {
        final Slot<LabelNode> loopStart = Slot.create(LabelNode.class);
        final Slot<LabelNode> loopEnd = Slot.create(LabelNode.class);
        final Slot<Integer> counter = Slot.create(Integer.class);
        return QueryStart.any(AbstractInsnNode.class).zeroOrMore(QueryStart.match(InstructionMatchers.anyInstruction())).then(InstructionMatchers.opCode(190).and(mutationPoint())).then(InstructionMatchers.opCode(54)).then(InstructionMatchers.opCode(3).and(mutationPoint())).then(InstructionMatchers.anIStore(counter.write()).and(InstructionMatchers.debug("store"))).then(InstructionMatchers.aLabelNode(loopStart.write())).then(InstructionMatchers.opCode(21)).then(InstructionMatchers.opCode(21)).then(InstructionMatchers.aConditionalJump().and(InstructionMatchers.jumpsTo(loopEnd.write())).and(mutationPoint())).zeroOrMore(QueryStart.match(InstructionMatchers.anyInstruction())).then(InstructionMatchers.incrementsVariable(counter.read()).and(mutationPoint())).then(InstructionMatchers.opCode(167).and(InstructionMatchers.jumpsTo(loopStart.read()))).zeroOrMore(QueryStart.match(InstructionMatchers.anyInstruction()));
    }
    
    private static Match<AbstractInsnNode> aMethodCallReturningAnIterator() {
        return InstructionMatchers.methodCallThatReturns(ClassName.fromClass(Iterator.class));
    }
    
    private static Match<AbstractInsnNode> mutationPoint() {
        return InstructionMatchers.recordTarget(ForEachLoopFilter.MUTATED_INSTRUCTION.read(), ForEachLoopFilter.FOUND.write());
    }
    
    private static Match<AbstractInsnNode> containMutation(final Slot<Boolean> found) {
        return new Match<AbstractInsnNode>() {
            @Override
            public boolean test(final Context<AbstractInsnNode> c, final AbstractInsnNode t) {
                return c.retrieve(found.read()).hasSome();
            }
        };
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
        return (Collection<MutationDetails>)FCollection.filter(mutations, (F<Object, Boolean>)Prelude.not(this.mutatesIteratorLoopPlumbing()));
    }
    
    private F<MutationDetails, Boolean> mutatesIteratorLoopPlumbing() {
        return new F<MutationDetails, Boolean>() {
            @Override
            public Boolean apply(final MutationDetails a) {
                final int instruction = a.getInstructionIndex();
                final MethodTree method = ForEachLoopFilter.this.currentClass.methods().findFirst(MethodMatchers.forLocation(a.getId().getLocation())).value();
                final AbstractInsnNode mutatedInstruction = method.instructions().get(instruction);
                final Context<AbstractInsnNode> context = Context.start(method.instructions(), false);
                context.store(ForEachLoopFilter.MUTATED_INSTRUCTION.write(), mutatedInstruction);
                return ForEachLoopFilter.ITERATOR_LOOP.matches(method.instructions(), context);
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
        FOUND = Slot.create(Boolean.class);
        ITERATOR_LOOP = QueryStart.match(Match.never()).or(conditionalAtStart()).or(conditionalAtEnd()).or(arrayConditionalAtEnd()).or(arrayConditionalAtStart()).then(containMutation(ForEachLoopFilter.FOUND)).compile(QueryParams.params(AbstractInsnNode.class).withIgnores(ForEachLoopFilter.IGNORE).withDebug(false));
    }
}
