// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

public interface ScheduledExecutorService extends ExecutorService
{
    ScheduledFuture schedule(final Runnable p0, final long p1, final TimeUnit p2);
    
    ScheduledFuture schedule(final Callable p0, final long p1, final TimeUnit p2);
    
    ScheduledFuture scheduleAtFixedRate(final Runnable p0, final long p1, final long p2, final TimeUnit p3);
    
    ScheduledFuture scheduleWithFixedDelay(final Runnable p0, final long p1, final long p2, final TimeUnit p3);
}
