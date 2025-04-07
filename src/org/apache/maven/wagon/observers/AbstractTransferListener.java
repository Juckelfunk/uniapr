// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon.observers;

import org.apache.maven.wagon.events.TransferEvent;
import org.apache.maven.wagon.events.TransferListener;

public abstract class AbstractTransferListener implements TransferListener
{
    public void transferInitiated(final TransferEvent transferEvent) {
    }
    
    public void transferStarted(final TransferEvent transferEvent) {
    }
    
    public void transferProgress(final TransferEvent transferEvent, final byte[] buffer, final int length) {
    }
    
    public void transferCompleted(final TransferEvent transferEvent) {
    }
    
    public void transferError(final TransferEvent transferEvent) {
    }
    
    public void debug(final String message) {
    }
}
