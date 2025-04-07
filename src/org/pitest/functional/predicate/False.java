// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.functional.predicate;

public class False<A> implements Predicate<A>
{
    private static final False<?> INSTANCE;
    
    public static <A> False<A> instance() {
        return (False<A>)False.INSTANCE;
    }
    
    @Override
    public Boolean apply(final A a) {
        return false;
    }
    
    static {
        INSTANCE = new False<Object>();
    }
}
