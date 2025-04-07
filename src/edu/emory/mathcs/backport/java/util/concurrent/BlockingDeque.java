// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

import java.util.Iterator;
import edu.emory.mathcs.backport.java.util.Deque;

public interface BlockingDeque extends BlockingQueue, Deque
{
    void addFirst(final Object p0);
    
    void addLast(final Object p0);
    
    boolean offerFirst(final Object p0);
    
    boolean offerLast(final Object p0);
    
    void putFirst(final Object p0) throws InterruptedException;
    
    void putLast(final Object p0) throws InterruptedException;
    
    boolean offerFirst(final Object p0, final long p1, final TimeUnit p2) throws InterruptedException;
    
    boolean offerLast(final Object p0, final long p1, final TimeUnit p2) throws InterruptedException;
    
    Object takeFirst() throws InterruptedException;
    
    Object takeLast() throws InterruptedException;
    
    Object pollFirst(final long p0, final TimeUnit p1) throws InterruptedException;
    
    Object pollLast(final long p0, final TimeUnit p1) throws InterruptedException;
    
    boolean removeFirstOccurrence(final Object p0);
    
    boolean removeLastOccurrence(final Object p0);
    
    boolean add(final Object p0);
    
    boolean offer(final Object p0);
    
    void put(final Object p0) throws InterruptedException;
    
    boolean offer(final Object p0, final long p1, final TimeUnit p2) throws InterruptedException;
    
    Object remove();
    
    Object poll();
    
    Object take() throws InterruptedException;
    
    Object poll(final long p0, final TimeUnit p1) throws InterruptedException;
    
    Object element();
    
    Object peek();
    
    boolean remove(final Object p0);
    
    boolean contains(final Object p0);
    
    int size();
    
    Iterator iterator();
    
    void push(final Object p0);
}
