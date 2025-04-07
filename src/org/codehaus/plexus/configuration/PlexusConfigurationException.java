// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.configuration;

public class PlexusConfigurationException extends Exception
{
    public PlexusConfigurationException(final String message) {
        this(message, null);
    }
    
    public PlexusConfigurationException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
