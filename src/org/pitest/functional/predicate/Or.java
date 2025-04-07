// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.functional.predicate;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class Or<A> implements Predicate<A>
{
    private final Set<Predicate<A>> ps;
    
    public Or(final Iterable<Predicate<A>> ps) {
        this.ps = new LinkedHashSet<Predicate<A>>();
        for (final Predicate<A> each : ps) {
            this.ps.add(each);
        }
    }
    
    @Override
    public Boolean apply(final A a) {
        for (final Predicate<A> each : this.ps) {
            if (each.apply(a)) {
                return true;
            }
        }
        return false;
    }
}
