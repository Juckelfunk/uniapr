// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.configuration.processor;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import java.util.Map;

public interface ConfigurationResourceHandler
{
    public static final String SOURCE = "source";
    
    String getId();
    
    PlexusConfiguration[] handleRequest(final Map p0) throws ConfigurationResourceNotFoundException, ConfigurationProcessingException;
}
