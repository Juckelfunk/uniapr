// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.functional;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.io.Serializable;

public abstract class Option<T> implements FunctionalIterable<T>, Serializable
{
    private static final long serialVersionUID = 1L;
    private static final None NONE;
    
    private Option() {
    }
    
    public abstract T value();
    
    public abstract T getOrElse(final T p0);
    
    public abstract boolean hasSome();
    
    @Override
    public boolean contains(final F<T, Boolean> predicate) {
        return FCollection.contains((Iterable<? extends T>)this, predicate);
    }
    
    @Override
    public FunctionalList<T> filter(final F<T, Boolean> predicate) {
        return FCollection.filter((Iterable<? extends T>)this, predicate);
    }
    
    @Override
    public <B> FunctionalList<B> flatMap(final F<T, ? extends Iterable<B>> f) {
        return FCollection.flatMap((Iterable<? extends T>)this, f);
    }
    
    @Override
    public void forEach(final SideEffect1<T> e) {
        FCollection.forEach((Iterable<? extends T>)this, e);
    }
    
    @Override
    public <B> FunctionalList<B> map(final F<T, B> f) {
        return FCollection.map((Iterable<? extends T>)this, f);
    }
    
    @Override
    public <B> void mapTo(final F<T, B> f, final Collection<? super B> bs) {
        FCollection.mapTo((Iterable<? extends T>)this, f, bs);
    }
    
    public static <T> Option<T> some(final T value) {
        if (value == null) {
            return (Option<T>)Option.NONE;
        }
        return new Some<T>((Object)value);
    }
    
    public static <T> None<T> none() {
        return (None<T>)Option.NONE;
    }
    
    public boolean hasNone() {
        return !this.hasSome();
    }
    
    static {
        NONE = new None();
    }
    
    public static final class None<T> extends Option<T>
    {
        private static final long serialVersionUID = 1L;
        
        private None() {
            super(null);
        }
        
        @Override
        public Iterator<T> iterator() {
            return Collections.emptySet().iterator();
        }
        
        @Override
        public T value() {
            throw new UnsupportedOperationException("Tried to retrieve value but had None.");
        }
        
        @Override
        public T getOrElse(final T defaultValue) {
            return defaultValue;
        }
        
        @Override
        public boolean hasSome() {
            return false;
        }
    }
    
    public static final class Some<T> extends Option<T>
    {
        private static final long serialVersionUID = 1L;
        private final T value;
        
        private Some(final T value) {
            super(null);
            this.value = value;
        }
        
        @Override
        public T value() {
            return this.value;
        }
        
        @Override
        public Iterator<T> iterator() {
            return Collections.singleton(this.value).iterator();
        }
        
        @Override
        public T getOrElse(final T defaultValue) {
            return this.value;
        }
        
        @Override
        public boolean hasSome() {
            return true;
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = 31 * result + ((this.value == null) ? 0 : this.value.hashCode());
            return result;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final Some other = (Some)obj;
            if (this.value == null) {
                if (other.value != null) {
                    return false;
                }
            }
            else if (!this.value.equals(other.value)) {
                return false;
            }
            return true;
        }
        
        @Override
        public String toString() {
            return "Some(" + this.value + ")";
        }
    }
}
