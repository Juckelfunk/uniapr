// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

import java.util.Collection;
import java.util.List;

public interface ExecutorService extends Executor
{
    void shutdown();
    
    List shutdownNow();
    
    boolean isShutdown();
    
    boolean isTerminated();
    
    boolean awaitTermination(final long p0, final TimeUnit p1) throws InterruptedException;
    
    Future submit(final Callable p0);
    
    Future submit(final Runnable p0, final Object p1);
    
    Future submit(final Runnable p0);
    
    List invokeAll(final Collection p0) throws InterruptedException;
    
    List invokeAll(final Collection p0, final long p1, final TimeUnit p2) throws InterruptedException;
    
    Object invokeAny(final Collection p0) throws InterruptedException, ExecutionException;
    
    Object invokeAny(final Collection p0, final long p1, final TimeUnit p2) throws InterruptedException, ExecutionException, TimeoutException;
}
