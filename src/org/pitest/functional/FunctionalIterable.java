// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.functional;

import java.util.Collection;
import java.util.List;

public interface FunctionalIterable<A> extends Iterable<A>
{
    void forEach(final SideEffect1<A> p0);
    
     <B> List<B> map(final F<A, B> p0);
    
     <B> void mapTo(final F<A, B> p0, final Collection<? super B> p1);
    
     <B> List<B> flatMap(final F<A, ? extends Iterable<B>> p0);
    
    List<A> filter(final F<A, Boolean> p0);
    
    boolean contains(final F<A, Boolean> p0);
}
