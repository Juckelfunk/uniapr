// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import java.util.Iterator;
import java.util.List;
import com.google.common.base.Preconditions;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(serializable = true, emulated = true)
final class SingletonImmutableList<E> extends ImmutableList<E>
{
    final transient E element;
    
    SingletonImmutableList(final E element) {
        this.element = Preconditions.checkNotNull(element);
    }
    
    @Override
    public E get(final int index) {
        Preconditions.checkElementIndex(index, 1);
        return this.element;
    }
    
    @Override
    public UnmodifiableIterator<E> iterator() {
        return Iterators.singletonIterator(this.element);
    }
    
    @Override
    public int size() {
        return 1;
    }
    
    @Override
    public ImmutableList<E> subList(final int fromIndex, final int toIndex) {
        Preconditions.checkPositionIndexes(fromIndex, toIndex, 1);
        return (fromIndex == toIndex) ? ImmutableList.of() : this;
    }
    
    @Override
    public String toString() {
        return '[' + this.element.toString() + ']';
    }
    
    @Override
    boolean isPartialView() {
        return false;
    }
}
