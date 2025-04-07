// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.personality.plexus.lifecycle.phase;

public class InitializationException extends Exception
{
    public InitializationException(final String message) {
        super(message);
    }
    
    public InitializationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
