// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util;

import java.util.Iterator;

public interface Deque extends Queue
{
    void addFirst(final Object p0);
    
    void addLast(final Object p0);
    
    boolean offerFirst(final Object p0);
    
    boolean offerLast(final Object p0);
    
    Object removeFirst();
    
    Object removeLast();
    
    Object pollFirst();
    
    Object pollLast();
    
    Object getFirst();
    
    Object getLast();
    
    Object peekFirst();
    
    Object peekLast();
    
    boolean removeFirstOccurrence(final Object p0);
    
    boolean removeLastOccurrence(final Object p0);
    
    boolean add(final Object p0);
    
    boolean offer(final Object p0);
    
    Object remove();
    
    Object poll();
    
    Object element();
    
    Object peek();
    
    void push(final Object p0);
    
    Object pop();
    
    boolean remove(final Object p0);
    
    boolean contains(final Object p0);
    
    int size();
    
    Iterator iterator();
    
    Iterator descendingIterator();
}
