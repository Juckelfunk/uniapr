// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.Map;
import java.util.Set;
import java.util.Collection;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public interface Multimap<K, V>
{
    int size();
    
    boolean isEmpty();
    
    boolean containsKey(@Nullable final Object p0);
    
    boolean containsValue(@Nullable final Object p0);
    
    boolean containsEntry(@Nullable final Object p0, @Nullable final Object p1);
    
    @CanIgnoreReturnValue
    boolean put(@Nullable final K p0, @Nullable final V p1);
    
    @CanIgnoreReturnValue
    boolean remove(@Nullable final Object p0, @Nullable final Object p1);
    
    @CanIgnoreReturnValue
    boolean putAll(@Nullable final K p0, final Iterable<? extends V> p1);
    
    @CanIgnoreReturnValue
    boolean putAll(final Multimap<? extends K, ? extends V> p0);
    
    @CanIgnoreReturnValue
    Collection<V> replaceValues(@Nullable final K p0, final Iterable<? extends V> p1);
    
    @CanIgnoreReturnValue
    Collection<V> removeAll(@Nullable final Object p0);
    
    void clear();
    
    Collection<V> get(@Nullable final K p0);
    
    Set<K> keySet();
    
    Multiset<K> keys();
    
    Collection<V> values();
    
    Collection<Map.Entry<K, V>> entries();
    
    Map<K, Collection<V>> asMap();
    
    boolean equals(@Nullable final Object p0);
    
    int hashCode();
}
