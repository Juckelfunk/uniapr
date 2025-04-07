// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util;

import java.util.Map;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractMap extends java.util.AbstractMap
{
    transient Set keySet;
    
    protected AbstractMap() {
    }
    
    public Set keySet() {
        if (this.keySet == null) {
            this.keySet = new AbstractSet() {
                private final /* synthetic */ AbstractMap this$0;
                
                public int size() {
                    return AbstractMap.this.size();
                }
                
                public boolean contains(final Object e) {
                    return AbstractMap.this.containsKey(e);
                }
                
                public Iterator iterator() {
                    return new Iterator() {
                        final Iterator itr = this.this$1.this$0.entrySet().iterator();
                        private final /* synthetic */ AbstractMap$1 this$1;
                        
                        public boolean hasNext() {
                            return this.itr.hasNext();
                        }
                        
                        public Object next() {
                            return this.itr.next().getKey();
                        }
                        
                        public void remove() {
                            this.itr.remove();
                        }
                    };
                }
            };
        }
        return this.keySet;
    }
    
    private static boolean eq(final Object o1, final Object o2) {
        return (o1 == null) ? (o2 == null) : o1.equals(o2);
    }
    
    public static class SimpleEntry implements Map.Entry
    {
        private final Object key;
        private Object value;
        
        public SimpleEntry(final Object key, final Object value) {
            this.key = key;
            this.value = value;
        }
        
        public SimpleEntry(final Map.Entry entry) {
            this.key = entry.getKey();
            this.value = entry.getValue();
        }
        
        public Object getKey() {
            return this.key;
        }
        
        public Object getValue() {
            return this.value;
        }
        
        public Object setValue(final Object value) {
            final Object oldValue = this.value;
            this.value = value;
            return oldValue;
        }
        
        public boolean equals(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry e = (Map.Entry)o;
            return eq(this.key, e.getKey()) && eq(this.value, e.getValue());
        }
        
        public int hashCode() {
            return ((this.key == null) ? 0 : this.key.hashCode()) ^ ((this.value == null) ? 0 : this.value.hashCode());
        }
        
        public String toString() {
            return this.key + "=" + this.value;
        }
    }
    
    public static class SimpleImmutableEntry implements Map.Entry
    {
        private final Object key;
        private final Object value;
        
        public SimpleImmutableEntry(final Object key, final Object value) {
            this.key = key;
            this.value = value;
        }
        
        public SimpleImmutableEntry(final Map.Entry entry) {
            this.key = entry.getKey();
            this.value = entry.getValue();
        }
        
        public Object getKey() {
            return this.key;
        }
        
        public Object getValue() {
            return this.value;
        }
        
        public Object setValue(final Object value) {
            throw new UnsupportedOperationException();
        }
        
        public boolean equals(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry e = (Map.Entry)o;
            return eq(this.key, e.getKey()) && eq(this.value, e.getValue());
        }
        
        public int hashCode() {
            return ((this.key == null) ? 0 : this.key.hashCode()) ^ ((this.value == null) ? 0 : this.value.hashCode());
        }
        
        public String toString() {
            return this.key + "=" + this.value;
        }
    }
}
