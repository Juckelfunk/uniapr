// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.configuration.processor;

public class ConfigurationResourceNotFoundException extends Exception
{
    public ConfigurationResourceNotFoundException(final String message) {
        super(message);
    }
    
    public ConfigurationResourceNotFoundException(final Throwable cause) {
        super(cause);
    }
    
    public ConfigurationResourceNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
