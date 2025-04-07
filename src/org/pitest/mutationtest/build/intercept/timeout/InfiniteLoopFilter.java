// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build.intercept.timeout;

import org.objectweb.asm.tree.FrameNode;
import org.pitest.bytecode.analysis.InstructionMatchers;
import org.objectweb.asm.tree.LineNumberNode;
import org.pitest.functional.Option;
import java.util.Collections;
import org.pitest.functional.F;
import org.pitest.bytecode.analysis.MethodMatchers;
import java.util.Iterator;
import java.util.List;
import org.pitest.mutationtest.engine.Location;
import java.util.Map;
import java.util.ArrayList;
import org.pitest.functional.FCollection;
import org.pitest.mutationtest.engine.Mutater;
import java.util.Collection;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.bytecode.analysis.MethodTree;
import org.pitest.sequence.SequenceMatcher;
import org.pitest.mutationtest.build.InterceptorType;
import org.pitest.bytecode.analysis.ClassTree;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.pitest.sequence.Match;
import org.pitest.mutationtest.build.MutationInterceptor;

public abstract class InfiniteLoopFilter implements MutationInterceptor
{
    static final Match<AbstractInsnNode> IGNORE;
    private ClassTree currentClass;
    
    @Override
    public InterceptorType type() {
        return InterceptorType.FILTER;
    }
    
    @Override
    public void begin(final ClassTree clazz) {
        this.currentClass = clazz;
    }
    
    abstract SequenceMatcher<AbstractInsnNode> infiniteLoopMatcher();
    
    abstract boolean couldCauseInfiniteLoop(final MethodTree p0, final MutationDetails p1);
    
    @Override
    public Collection<MutationDetails> intercept(final Collection<MutationDetails> mutations, final Mutater m) {
        final Map<Location, Collection<MutationDetails>> buckets = FCollection.bucket(mutations, this.mutationToLocation());
        final List<MutationDetails> willTimeout = new ArrayList<MutationDetails>();
        for (final Map.Entry<Location, Collection<MutationDetails>> each : buckets.entrySet()) {
            willTimeout.addAll(this.findTimeoutMutants(each.getKey(), each.getValue(), m));
        }
        mutations.removeAll(willTimeout);
        return mutations;
    }
    
    private Collection<MutationDetails> findTimeoutMutants(final Location location, final Collection<MutationDetails> mutations, final Mutater m) {
        final MethodTree method = this.currentClass.methods().findFirst(MethodMatchers.forLocation(location)).value();
        if (this.infiniteLoopMatcher().matches(method.instructions())) {
            return (Collection<MutationDetails>)Collections.emptyList();
        }
        final List<MutationDetails> timeouts = new ArrayList<MutationDetails>();
        for (final MutationDetails each : mutations) {
            if (this.couldCauseInfiniteLoop(method, each) && this.isInfiniteLoop(each, m)) {
                timeouts.add(each);
            }
        }
        return timeouts;
    }
    
    private boolean isInfiniteLoop(final MutationDetails each, final Mutater m) {
        final ClassTree mutantClass = ClassTree.fromBytes(m.getMutation(each.getId()).getBytes());
        final Option<MethodTree> mutantMethod = mutantClass.methods().findFirst(MethodMatchers.forLocation(each.getId().getLocation()));
        return this.infiniteLoopMatcher().matches(mutantMethod.value().instructions());
    }
    
    private F<MutationDetails, Location> mutationToLocation() {
        return new F<MutationDetails, Location>() {
            @Override
            public Location apply(final MutationDetails a) {
                return a.getId().getLocation();
            }
        };
    }
    
    @Override
    public void end() {
        this.currentClass = null;
    }
    
    static {
        IGNORE = InstructionMatchers.isA(LineNumberNode.class).or(InstructionMatchers.isA(FrameNode.class));
    }
}
