// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent.locks;

import java.util.Date;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

public interface Condition
{
    void await() throws InterruptedException;
    
    void awaitUninterruptibly();
    
    boolean await(final long p0, final TimeUnit p1) throws InterruptedException;
    
    boolean awaitUntil(final Date p0) throws InterruptedException;
    
    void signal();
    
    void signalAll();
}
