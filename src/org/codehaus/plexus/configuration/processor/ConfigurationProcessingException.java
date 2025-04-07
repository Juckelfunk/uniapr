// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.configuration.processor;

public class ConfigurationProcessingException extends Exception
{
    public ConfigurationProcessingException(final String message) {
        super(message);
    }
    
    public ConfigurationProcessingException(final Throwable cause) {
        super(cause);
    }
    
    public ConfigurationProcessingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
