// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.testapi;

public interface ResultCollector
{
    void notifyEnd(final Description p0, final Throwable p1);
    
    void notifyEnd(final Description p0);
    
    void notifyStart(final Description p0);
    
    void notifySkipped(final Description p0);
    
    boolean shouldExit();
}
