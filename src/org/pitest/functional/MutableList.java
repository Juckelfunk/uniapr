// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.functional;

import java.util.ListIterator;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class MutableList<A> implements FunctionalList<A>
{
    private static final long serialVersionUID = 1L;
    private final List<A> impl;
    
    @SafeVarargs
    public MutableList(final A... as) {
        this(Arrays.asList(as));
    }
    
    public MutableList(final List<A> impl) {
        this.impl = impl;
    }
    
    public MutableList() {
        this((List)new ArrayList());
    }
    
    @Override
    public boolean add(final A o) {
        return this.impl.add(o);
    }
    
    @Override
    public boolean addAll(final Collection<? extends A> c) {
        return this.impl.addAll(c);
    }
    
    @Override
    public void clear() {
        this.impl.clear();
    }
    
    @Override
    public boolean contains(final Object o) {
        return this.impl.contains(o);
    }
    
    @Override
    public boolean containsAll(final Collection<?> c) {
        return this.impl.containsAll(c);
    }
    
    @Override
    public boolean isEmpty() {
        return this.impl.isEmpty();
    }
    
    @Override
    public Iterator<A> iterator() {
        return this.impl.iterator();
    }
    
    @Override
    public boolean remove(final Object o) {
        return this.impl.remove(o);
    }
    
    @Override
    public boolean removeAll(final Collection<?> c) {
        return this.impl.removeAll(c);
    }
    
    @Override
    public boolean retainAll(final Collection<?> c) {
        return this.impl.retainAll(c);
    }
    
    @Override
    public int size() {
        return this.impl.size();
    }
    
    @Override
    public Object[] toArray() {
        return this.impl.toArray();
    }
    
    @Override
    public <T> T[] toArray(final T[] a) {
        return this.impl.toArray(a);
    }
    
    @Override
    public boolean contains(final F<A, Boolean> predicate) {
        return FCollection.contains((Iterable<? extends A>)this, predicate);
    }
    
    @Override
    public FunctionalList<A> filter(final F<A, Boolean> predicate) {
        return FCollection.filter((Iterable<? extends A>)this, predicate);
    }
    
    @Override
    public <B> FunctionalList<B> flatMap(final F<A, ? extends Iterable<B>> f) {
        return FCollection.flatMap((Iterable<? extends A>)this, f);
    }
    
    @Override
    public void forEach(final SideEffect1<A> e) {
        FCollection.forEach((Iterable<? extends A>)this, e);
    }
    
    @Override
    public <B> FunctionalList<B> map(final F<A, B> f) {
        return FCollection.map((Iterable<? extends A>)this, f);
    }
    
    @Override
    public <B> void mapTo(final F<A, B> f, final Collection<? super B> bs) {
        FCollection.mapTo((Iterable<? extends A>)this, f, bs);
    }
    
    @Override
    public Option<A> findFirst(final F<A, Boolean> predicate) {
        return FCollection.findFirst((Iterable<? extends A>)this.impl, predicate);
    }
    
    @Override
    public void add(final int arg0, final A arg1) {
        this.impl.add(arg0, arg1);
    }
    
    @Override
    public boolean addAll(final int arg0, final Collection<? extends A> arg1) {
        return this.impl.addAll(arg0, arg1);
    }
    
    @Override
    public A get(final int index) {
        return this.impl.get(index);
    }
    
    @Override
    public int indexOf(final Object arg0) {
        return this.impl.indexOf(arg0);
    }
    
    @Override
    public int lastIndexOf(final Object arg0) {
        return this.impl.lastIndexOf(arg0);
    }
    
    @Override
    public ListIterator<A> listIterator() {
        return this.impl.listIterator();
    }
    
    @Override
    public ListIterator<A> listIterator(final int index) {
        return this.impl.listIterator(index);
    }
    
    @Override
    public A remove(final int index) {
        return this.impl.remove(index);
    }
    
    @Override
    public A set(final int index, final A element) {
        return this.impl.set(index, element);
    }
    
    @Override
    public List<A> subList(final int fromIndex, final int toIndex) {
        return this.impl.subList(fromIndex, toIndex);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.impl == null) ? 0 : this.impl.hashCode());
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
        final MutableList other = (MutableList)obj;
        if (this.impl == null) {
            if (other.impl != null) {
                return false;
            }
        }
        else if (!this.impl.equals(other.impl)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return this.impl.toString();
    }
}
