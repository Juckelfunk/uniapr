// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.io.Serializable;
import com.google.common.annotations.GwtIncompatible;
import java.util.Iterator;
import javax.annotation.Nullable;
import java.util.Map;
import com.google.j2objc.annotations.Weak;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
final class ImmutableMapValues<K, V> extends ImmutableCollection<V>
{
    @Weak
    private final ImmutableMap<K, V> map;
    
    ImmutableMapValues(final ImmutableMap<K, V> map) {
        this.map = map;
    }
    
    @Override
    public int size() {
        return this.map.size();
    }
    
    @Override
    public UnmodifiableIterator<V> iterator() {
        return new UnmodifiableIterator<V>() {
            final UnmodifiableIterator<Map.Entry<K, V>> entryItr = ImmutableMapValues.this.map.entrySet().iterator();
            
            @Override
            public boolean hasNext() {
                return this.entryItr.hasNext();
            }
            
            @Override
            public V next() {
                return this.entryItr.next().getValue();
            }
        };
    }
    
    @Override
    public boolean contains(@Nullable final Object object) {
        return object != null && Iterators.contains(this.iterator(), object);
    }
    
    @Override
    boolean isPartialView() {
        return true;
    }
    
    @Override
    public ImmutableList<V> asList() {
        final ImmutableList<Map.Entry<K, V>> entryList = this.map.entrySet().asList();
        return new ImmutableAsList<V>() {
            @Override
            public V get(final int index) {
                return ((Map.Entry)entryList.get(index)).getValue();
            }
            
            @Override
            ImmutableCollection<V> delegateCollection() {
                return (ImmutableCollection<V>)ImmutableMapValues.this;
            }
        };
    }
    
    @GwtIncompatible
    @Override
    Object writeReplace() {
        return new SerializedForm((ImmutableMap<?, Object>)this.map);
    }
    
    @GwtIncompatible
    private static class SerializedForm<V> implements Serializable
    {
        final ImmutableMap<?, V> map;
        private static final long serialVersionUID = 0L;
        
        SerializedForm(final ImmutableMap<?, V> map) {
            this.map = map;
        }
        
        Object readResolve() {
            return this.map.values();
        }
    }
}
