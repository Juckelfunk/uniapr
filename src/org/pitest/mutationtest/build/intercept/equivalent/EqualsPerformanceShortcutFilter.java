// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build.intercept.equivalent;

import org.pitest.sequence.QueryParams;
import org.pitest.sequence.QueryStart;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.pitest.mutationtest.engine.MethodName;
import org.pitest.sequence.Context;
import org.pitest.bytecode.analysis.InstructionMatchers;
import org.pitest.functional.Option;
import org.pitest.mutationtest.engine.Location;
import org.pitest.bytecode.analysis.MethodTree;
import org.pitest.bytecode.analysis.MethodMatchers;
import java.util.List;
import org.pitest.functional.FunctionalList;
import org.pitest.functional.F;
import org.pitest.functional.FCollection;
import org.pitest.functional.prelude.Prelude;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;
import java.util.Collection;
import org.pitest.mutationtest.build.InterceptorType;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.sequence.SequenceMatcher;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.pitest.sequence.Match;
import org.pitest.mutationtest.build.MutationInterceptor;

public class EqualsPerformanceShortcutFilter implements MutationInterceptor
{
    private static final boolean DEBUG = false;
    private static final Match<AbstractInsnNode> IGNORE;
    static final SequenceMatcher<AbstractInsnNode> ALWAYS_FALSE;
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
        final FunctionalList<MutationDetails> doNotTouch = FCollection.filter(mutations, (F<MutationDetails, Boolean>)Prelude.not(this.inEqualsMethod()));
        if (doNotTouch.size() != mutations.size()) {
            final FunctionalList<MutationDetails> inEquals = FCollection.filter(mutations, this.inEqualsMethod());
            final List<MutationDetails> filtered = this.filter(inEquals, m);
            doNotTouch.addAll((Collection<?>)filtered);
        }
        return doNotTouch;
    }
    
    private List<MutationDetails> filter(final FunctionalList<MutationDetails> inEquals, final Mutater m) {
        final Location equalsMethod = inEquals.get(0).getId().getLocation();
        final Option<MethodTree> maybeEquals = this.currentClass.methods().findFirst(MethodMatchers.forLocation(equalsMethod));
        return inEquals.filter((F<MutationDetails, Boolean>)Prelude.not(this.isShortcutEquals(maybeEquals.value(), m)));
    }
    
    private F<MutationDetails, Boolean> isShortcutEquals(final MethodTree tree, final Mutater m) {
        return new F<MutationDetails, Boolean>() {
            @Override
            public Boolean apply(final MutationDetails a) {
                return EqualsPerformanceShortcutFilter.this.shortCutEquals(tree, a, m);
            }
        };
    }
    
    private Boolean shortCutEquals(final MethodTree tree, final MutationDetails a, final Mutater m) {
        if (!this.mutatesAConditionalJump(tree, a.getInstructionIndex())) {
            return false;
        }
        final ClassTree mutant = ClassTree.fromBytes(m.getMutation(a.getId()).getBytes());
        final MethodTree mutantEquals = mutant.methods().findFirst(MethodMatchers.forLocation(tree.asLocation())).value();
        return EqualsPerformanceShortcutFilter.ALWAYS_FALSE.matches(mutantEquals.instructions());
    }
    
    private boolean mutatesAConditionalJump(final MethodTree tree, final int index) {
        final AbstractInsnNode mutatedInsns = tree.instructions().get(index);
        return InstructionMatchers.aConditionalJump().test(null, mutatedInsns);
    }
    
    private F<MutationDetails, Boolean> inEqualsMethod() {
        return new F<MutationDetails, Boolean>() {
            @Override
            public Boolean apply(final MutationDetails a) {
                final Location loc = a.getId().getLocation();
                return loc.getMethodDesc().equals("(Ljava/lang/Object;)Z") && loc.getMethodName().equals(MethodName.fromString("equals"));
            }
        };
    }
    
    @Override
    public void end() {
    }
    
    static {
        IGNORE = InstructionMatchers.isA(LineNumberNode.class).or(InstructionMatchers.isA(FrameNode.class));
        ALWAYS_FALSE = QueryStart.any(AbstractInsnNode.class).then(InstructionMatchers.opCode(25)).then(InstructionMatchers.opCode(25)).then(InstructionMatchers.opCode(88)).then(InstructionMatchers.opCode(167).and(InstructionMatchers.debug("goto"))).zeroOrMore(QueryStart.match(InstructionMatchers.anyInstruction())).compile(QueryParams.params(AbstractInsnNode.class).withIgnores(EqualsPerformanceShortcutFilter.IGNORE).withDebug(false));
    }
}
