// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon.events;

public interface TransferListener
{
    void transferInitiated(final TransferEvent p0);
    
    void transferStarted(final TransferEvent p0);
    
    void transferProgress(final TransferEvent p0, final byte[] p1, final int p2);
    
    void transferCompleted(final TransferEvent p0);
    
    void transferError(final TransferEvent p0);
    
    void debug(final String p0);
}
