// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus;

public class PlexusContainerException extends Exception
{
    public PlexusContainerException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
    
    public PlexusContainerException(final String message) {
        super(message);
    }
}
