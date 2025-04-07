// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon.events;

public interface SessionListener
{
    void sessionOpening(final SessionEvent p0);
    
    void sessionOpened(final SessionEvent p0);
    
    void sessionDisconnecting(final SessionEvent p0);
    
    void sessionDisconnected(final SessionEvent p0);
    
    void sessionConnectionRefused(final SessionEvent p0);
    
    void sessionLoggedIn(final SessionEvent p0);
    
    void sessionLoggedOff(final SessionEvent p0);
    
    void sessionError(final SessionEvent p0);
    
    void debug(final String p0);
}
