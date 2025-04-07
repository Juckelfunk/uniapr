// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.functional.predicate;

import java.util.Iterator;
import java.util.LinkedHashSet;
import org.pitest.functional.F;
import java.util.Set;

public class And<A> implements Predicate<A>
{
    private final Set<F<A, Boolean>> ps;
    
    public And(final Iterable<? extends F<A, Boolean>> ps) {
        this.ps = new LinkedHashSet<F<A, Boolean>>();
        for (final F<A, Boolean> each : ps) {
            this.ps.add(each);
        }
    }
    
    @Override
    public Boolean apply(final A a) {
        for (final F<A, Boolean> each : this.ps) {
            if (!each.apply(a)) {
                return false;
            }
        }
        return !this.ps.isEmpty();
    }
}
