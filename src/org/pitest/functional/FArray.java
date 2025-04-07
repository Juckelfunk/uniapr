// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.functional;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

public abstract class FArray
{
    public static <T> void filter(final T[] xs, final F<T, Boolean> predicate, final Collection<T> dest) {
        if (xs != null) {
            for (final T x : xs) {
                if (predicate.apply(x)) {
                    dest.add(x);
                }
            }
        }
    }
    
    public static <T> List<T> filter(final T[] xs, final F<T, Boolean> predicate) {
        final List<T> dest = new ArrayList<T>();
        filter(xs, predicate, dest);
        return dest;
    }
    
    public static <T> boolean contains(final T[] xs, final F<T, Boolean> predicate) {
        for (final T x : xs) {
            if (predicate.apply(x)) {
                return true;
            }
        }
        return false;
    }
    
    public static <A, B> void flatMapTo(final A[] as, final F<A, ? extends Iterable<B>> f, final Collection<? super B> bs) {
        if (as != null) {
            for (final A a : as) {
                for (final B each : (Iterable)f.apply(a)) {
                    bs.add((Object)each);
                }
            }
        }
    }
    
    public static <A, B> FunctionalList<B> flatMap(final A[] as, final F<A, ? extends Iterable<B>> f) {
        final FunctionalList<B> bs = emptyList();
        flatMapTo(as, (F<A, ? extends Iterable<Object>>)f, (Collection<? super Object>)bs);
        return bs;
    }
    
    private static <T> FunctionalList<T> emptyList() {
        return new MutableList<T>();
    }
}
