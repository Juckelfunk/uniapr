// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.Iterator;
import java.util.Set;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;
import java.util.Collection;

@GwtCompatible
public interface Multiset<E> extends Collection<E>
{
    int count(@Nullable final Object p0);
    
    @CanIgnoreReturnValue
    int add(@Nullable final E p0, final int p1);
    
    @CanIgnoreReturnValue
    int remove(@Nullable final Object p0, final int p1);
    
    @CanIgnoreReturnValue
    int setCount(final E p0, final int p1);
    
    @CanIgnoreReturnValue
    boolean setCount(final E p0, final int p1, final int p2);
    
    Set<E> elementSet();
    
    Set<Entry<E>> entrySet();
    
    boolean equals(@Nullable final Object p0);
    
    int hashCode();
    
    String toString();
    
    Iterator<E> iterator();
    
    boolean contains(@Nullable final Object p0);
    
    boolean containsAll(final Collection<?> p0);
    
    @CanIgnoreReturnValue
    boolean add(final E p0);
    
    @CanIgnoreReturnValue
    boolean remove(@Nullable final Object p0);
    
    @CanIgnoreReturnValue
    boolean removeAll(final Collection<?> p0);
    
    @CanIgnoreReturnValue
    boolean retainAll(final Collection<?> p0);
    
    public interface Entry<E>
    {
        E getElement();
        
        int getCount();
        
        boolean equals(final Object p0);
        
        int hashCode();
        
        String toString();
    }
}
