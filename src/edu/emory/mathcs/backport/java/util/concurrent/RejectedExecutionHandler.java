// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

public interface RejectedExecutionHandler
{
    void rejectedExecution(final Runnable p0, final ThreadPoolExecutor p1);
}
