// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

import java.util.Collection;
import edu.emory.mathcs.backport.java.util.Queue;

public interface BlockingQueue extends Queue
{
    boolean add(final Object p0);
    
    boolean offer(final Object p0);
    
    void put(final Object p0) throws InterruptedException;
    
    boolean offer(final Object p0, final long p1, final TimeUnit p2) throws InterruptedException;
    
    Object take() throws InterruptedException;
    
    Object poll(final long p0, final TimeUnit p1) throws InterruptedException;
    
    int remainingCapacity();
    
    boolean remove(final Object p0);
    
    boolean contains(final Object p0);
    
    int drainTo(final Collection p0);
    
    int drainTo(final Collection p0, final int p1);
}
