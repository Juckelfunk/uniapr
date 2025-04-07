// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.personality.plexus.lifecycle.phase;

public class StartingException extends Exception
{
    public StartingException(final String message) {
        super(message);
    }
    
    public StartingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
