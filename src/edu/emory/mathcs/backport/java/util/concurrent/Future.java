// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

public interface Future
{
    boolean cancel(final boolean p0);
    
    boolean isCancelled();
    
    boolean isDone();
    
    Object get() throws InterruptedException, ExecutionException;
    
    Object get(final long p0, final TimeUnit p1) throws InterruptedException, ExecutionException, TimeoutException;
}
