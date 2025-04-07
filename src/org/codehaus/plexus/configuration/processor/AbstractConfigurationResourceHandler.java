// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.configuration.processor;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import java.util.Map;

public abstract class AbstractConfigurationResourceHandler implements ConfigurationResourceHandler
{
    protected String getSource(final Map parameters) throws ConfigurationProcessingException {
        final String source = parameters.get("source");
        if (source == null) {
            throw new ConfigurationProcessingException("The 'source' attribute for a configuration resource handler cannot be null.");
        }
        return source;
    }
}
