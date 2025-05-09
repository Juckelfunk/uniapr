// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.io.Serializable;
import java.util.Iterator;
import com.google.j2objc.annotations.Weak;
import com.google.common.annotations.GwtIncompatible;
import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;
import java.util.Map;

@GwtCompatible(emulated = true)
abstract class ImmutableMapEntrySet<K, V> extends ImmutableSet<Map.Entry<K, V>>
{
    abstract ImmutableMap<K, V> map();
    
    @Override
    public int size() {
        return this.map().size();
    }
    
    @Override
    public boolean contains(@Nullable final Object object) {
        if (object instanceof Map.Entry) {
            final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)object;
            final V value = this.map().get(entry.getKey());
            return value != null && value.equals(entry.getValue());
        }
        return false;
    }
    
    @Override
    boolean isPartialView() {
        return this.map().isPartialView();
    }
    
    @GwtIncompatible
    @Override
    boolean isHashCodeFast() {
        return this.map().isHashCodeFast();
    }
    
    @Override
    public int hashCode() {
        return this.map().hashCode();
    }
    
    @GwtIncompatible
    @Override
    Object writeReplace() {
        return new EntrySetSerializedForm(this.map());
    }
    
    static final class RegularEntrySet<K, V> extends ImmutableMapEntrySet<K, V>
    {
        @Weak
        private final transient ImmutableMap<K, V> map;
        private final transient Map.Entry<K, V>[] entries;
        
        RegularEntrySet(final ImmutableMap<K, V> map, final Map.Entry<K, V>[] entries) {
            this.map = map;
            this.entries = entries;
        }
        
        @Override
        ImmutableMap<K, V> map() {
            return this.map;
        }
        
        @Override
        public UnmodifiableIterator<Map.Entry<K, V>> iterator() {
            return Iterators.forArray(this.entries);
        }
        
        @Override
        ImmutableList<Map.Entry<K, V>> createAsList() {
            return new RegularImmutableAsList<Map.Entry<K, V>>((ImmutableCollection<Map.Entry<K, V>>)this, this.entries);
        }
    }
    
    @GwtIncompatible
    private static class EntrySetSerializedForm<K, V> implements Serializable
    {
        final ImmutableMap<K, V> map;
        private static final long serialVersionUID = 0L;
        
        EntrySetSerializedForm(final ImmutableMap<K, V> map) {
            this.map = map;
        }
        
        Object readResolve() {
            return this.map.entrySet();
        }
    }
}
