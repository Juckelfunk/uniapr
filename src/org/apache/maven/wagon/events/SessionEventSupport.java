// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon.events;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public final class SessionEventSupport
{
    private final List listeners;
    
    public SessionEventSupport() {
        this.listeners = new ArrayList();
    }
    
    public void addSessionListener(final SessionListener listener) {
        if (listener != null) {
            this.listeners.add(listener);
        }
    }
    
    public void removeSessionListener(final SessionListener listener) {
        this.listeners.remove(listener);
    }
    
    public boolean hasSessionListener(final SessionListener listener) {
        return this.listeners.contains(listener);
    }
    
    public void fireSessionDisconnected(final SessionEvent sessionEvent) {
        for (final SessionListener listener : this.listeners) {
            listener.sessionDisconnected(sessionEvent);
        }
    }
    
    public void fireSessionDisconnecting(final SessionEvent sessionEvent) {
        for (final SessionListener listener : this.listeners) {
            listener.sessionDisconnecting(sessionEvent);
        }
    }
    
    public void fireSessionLoggedIn(final SessionEvent sessionEvent) {
        for (final SessionListener listener : this.listeners) {
            listener.sessionLoggedIn(sessionEvent);
        }
    }
    
    public void fireSessionLoggedOff(final SessionEvent sessionEvent) {
        for (final SessionListener listener : this.listeners) {
            listener.sessionLoggedOff(sessionEvent);
        }
    }
    
    public void fireSessionOpened(final SessionEvent sessionEvent) {
        for (final SessionListener listener : this.listeners) {
            listener.sessionOpened(sessionEvent);
        }
    }
    
    public void fireSessionOpening(final SessionEvent sessionEvent) {
        for (final SessionListener listener : this.listeners) {
            listener.sessionOpening(sessionEvent);
        }
    }
    
    public void fireSessionConnectionRefused(final SessionEvent sessionEvent) {
        for (final SessionListener listener : this.listeners) {
            listener.sessionConnectionRefused(sessionEvent);
        }
    }
    
    public void fireDebug(final String message) {
        for (final SessionListener listener : this.listeners) {
            listener.debug(message);
        }
    }
    
    public void fireSessionError(final SessionEvent sessionEvent) {
        for (final SessionListener listener : this.listeners) {
            listener.sessionError(sessionEvent);
        }
    }
}
