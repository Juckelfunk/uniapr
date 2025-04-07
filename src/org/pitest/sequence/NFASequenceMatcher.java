// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.sequence;

import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

class NFASequenceMatcher<T> implements SequenceMatcher<T>
{
    private final boolean debug;
    private final Match<T> ignore;
    private final State<T> start;
    
    NFASequenceMatcher(final Match<T> ignore, final State<T> state, final boolean debug) {
        this.ignore = ignore;
        this.start = state;
        this.debug = debug;
    }
    
    @Override
    public boolean matches(final List<T> sequence) {
        return this.matches(sequence, Context.start(sequence, this.debug));
    }
    
    @Override
    public boolean matches(final List<T> sequence, final Context<T> context) {
        Set<State<T>> currentState = new HashSet<State<T>>();
        addstate(currentState, this.start);
        for (final T t : sequence) {
            context.moveForward();
            if (this.ignore.test(context, t)) {
                continue;
            }
            final Set<State<T>> nextStates = currentState = step(context, currentState, t);
        }
        return isMatch(currentState);
    }
    
    private static <T> void addstate(final Set<State<T>> set, final State<T> state) {
        if (state == null) {
            return;
        }
        if (state instanceof Split) {
            final Split<T> split = (Split<T>)(Split)state;
            addstate((Set<State<Object>>)set, (State<Object>)split.out1);
            addstate((Set<State<Object>>)set, (State<Object>)split.out2);
        }
        else {
            set.add(state);
        }
    }
    
    private static <T> Set<State<T>> step(final Context<T> context, final Set<State<T>> currentState, final T c) {
        final Set<State<T>> nextStates = new HashSet<State<T>>();
        for (final State<T> each : currentState) {
            if (each instanceof Consume) {
                final Consume<T> consume = (Consume<T>)(Consume)each;
                if (!consume.c.test(context, c)) {
                    continue;
                }
                addstate(nextStates, consume.out);
            }
        }
        return nextStates;
    }
    
    private static <T> boolean isMatch(final Set<State<T>> currentState) {
        return currentState.contains(EndMatch.MATCH);
    }
}
