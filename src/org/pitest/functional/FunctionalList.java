// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.functional;

import java.io.Serializable;
import java.util.List;

public interface FunctionalList<T> extends FunctionalCollection<T>, List<T>, Serializable
{
    FunctionalList<T> filter(final F<T, Boolean> p0);
    
     <B> FunctionalList<B> flatMap(final F<T, ? extends Iterable<B>> p0);
    
     <B> FunctionalList<B> map(final F<T, B> p0);
    
    Option<T> findFirst(final F<T, Boolean> p0);
}
