// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon.events;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public final class TransferEventSupport
{
    private final List listeners;
    
    public TransferEventSupport() {
        this.listeners = new ArrayList();
    }
    
    public synchronized void addTransferListener(final TransferListener listener) {
        if (listener != null) {
            this.listeners.add(listener);
        }
    }
    
    public synchronized void removeTransferListener(final TransferListener listener) {
        this.listeners.remove(listener);
    }
    
    public synchronized boolean hasTransferListener(final TransferListener listener) {
        return this.listeners.contains(listener);
    }
    
    public synchronized void fireTransferStarted(final TransferEvent transferEvent) {
        for (final TransferListener listener : this.listeners) {
            listener.transferStarted(transferEvent);
        }
    }
    
    public synchronized void fireTransferProgress(final TransferEvent transferEvent, final byte[] buffer, final int length) {
        for (final TransferListener listener : this.listeners) {
            listener.transferProgress(transferEvent, buffer, length);
        }
    }
    
    public synchronized void fireTransferCompleted(final TransferEvent transferEvent) {
        for (final TransferListener listener : this.listeners) {
            listener.transferCompleted(transferEvent);
        }
    }
    
    public synchronized void fireTransferError(final TransferEvent transferEvent) {
        for (final TransferListener listener : this.listeners) {
            listener.transferError(transferEvent);
        }
    }
    
    public synchronized void fireDebug(final String message) {
        for (final TransferListener listener : this.listeners) {
            listener.debug(message);
        }
    }
    
    public synchronized void fireTransferInitiated(final TransferEvent transferEvent) {
        for (final TransferListener listener : this.listeners) {
            listener.transferInitiated(transferEvent);
        }
    }
}
