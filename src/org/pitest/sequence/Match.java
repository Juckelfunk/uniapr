// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.sequence;

public abstract class Match<T>
{
    public abstract boolean test(final Context<T> p0, final T p1);
    
    public static <T> Match<T> always() {
        return new Match<T>() {
            @Override
            public boolean test(final Context<T> c, final T t) {
                return true;
            }
        };
    }
    
    public static <T> Match<T> never() {
        return new Match<T>() {
            @Override
            public boolean test(final Context<T> c, final T t) {
                return false;
            }
        };
    }
    
    public static <T> Match<T> isEqual(final Object targetRef) {
        return new Match<T>() {
            @Override
            public boolean test(final Context<T> c, final T t) {
                return targetRef.equals(t);
            }
        };
    }
    
    public Match<T> and(final Match<T> other) {
        final Match<T> self = this;
        return new Match<T>() {
            @Override
            public boolean test(final Context<T> c, final T t) {
                return self.test(c, t) && other.test(c, t);
            }
        };
    }
    
    public Match<T> negate() {
        final Match<T> self = this;
        return new Match<T>() {
            @Override
            public boolean test(final Context<T> c, final T t) {
                return !self.test(c, t);
            }
        };
    }
    
    public Match<T> or(final Match<T> other) {
        final Match<T> self = this;
        return new Match<T>() {
            @Override
            public boolean test(final Context<T> c, final T t) {
                return self.test(c, t) || other.test(c, t);
            }
        };
    }
}
