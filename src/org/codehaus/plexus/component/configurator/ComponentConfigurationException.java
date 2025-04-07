// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator;

import org.codehaus.plexus.configuration.PlexusConfiguration;

public class ComponentConfigurationException extends Exception
{
    private PlexusConfiguration failedConfiguration;
    
    public ComponentConfigurationException(final String message) {
        super(message);
    }
    
    public ComponentConfigurationException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public ComponentConfigurationException(final Throwable cause) {
        super(cause);
    }
    
    public ComponentConfigurationException(final PlexusConfiguration failedConfiguration, final String message) {
        super(message);
        this.failedConfiguration = failedConfiguration;
    }
    
    public ComponentConfigurationException(final PlexusConfiguration failedConfiguration, final String message, final Throwable cause) {
        super(message, cause);
        this.failedConfiguration = failedConfiguration;
    }
    
    public ComponentConfigurationException(final PlexusConfiguration failedConfiguration, final Throwable cause) {
        super(cause);
        this.failedConfiguration = failedConfiguration;
    }
    
    public void setFailedConfiguration(final PlexusConfiguration failedConfiguration) {
        this.failedConfiguration = failedConfiguration;
    }
    
    public PlexusConfiguration getFailedConfiguration() {
        return this.failedConfiguration;
    }
}
