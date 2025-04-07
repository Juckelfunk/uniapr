// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.functional.predicate;

import org.pitest.functional.F;

public final class Not<A> implements Predicate<A>
{
    private final F<A, Boolean> p;
    
    public Not(final F<A, Boolean> p) {
        this.p = p;
    }
    
    @Override
    public Boolean apply(final A a) {
        return !this.p.apply(a);
    }
}
