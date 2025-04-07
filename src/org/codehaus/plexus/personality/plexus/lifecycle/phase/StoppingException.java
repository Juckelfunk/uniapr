// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.personality.plexus.lifecycle.phase;

public class StoppingException extends Exception
{
    public StoppingException(final String message) {
        super(message);
    }
    
    public StoppingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
