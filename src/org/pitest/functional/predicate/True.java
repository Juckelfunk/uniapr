// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.functional.predicate;

public class True<A> implements Predicate<A>
{
    private static final True<?> INSTANCE;
    
    public static <A> Predicate<A> all() {
        return (Predicate<A>)True.INSTANCE;
    }
    
    @Override
    public Boolean apply(final A a) {
        return true;
    }
    
    static {
        INSTANCE = new True<Object>();
    }
}
