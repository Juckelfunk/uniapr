// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

public interface CompletionService
{
    Future submit(final Callable p0);
    
    Future submit(final Runnable p0, final Object p1);
    
    Future take() throws InterruptedException;
    
    Future poll();
    
    Future poll(final long p0, final TimeUnit p1) throws InterruptedException;
}
