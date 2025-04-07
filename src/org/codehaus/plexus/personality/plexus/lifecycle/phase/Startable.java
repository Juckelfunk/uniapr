// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.personality.plexus.lifecycle.phase;

public interface Startable
{
    void start() throws StartingException;
    
    void stop() throws StoppingException;
}
