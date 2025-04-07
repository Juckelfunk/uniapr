// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.embed;

import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.configuration.PlexusConfigurationResourceException;
import org.codehaus.plexus.PlexusContainerException;
import java.io.File;
import java.util.Properties;
import java.io.Reader;
import java.io.IOException;
import java.net.URL;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.PlexusContainer;

public interface PlexusEmbedder
{
    PlexusContainer getContainer();
    
    Object lookup(final String p0) throws ComponentLookupException;
    
    Object lookup(final String p0, final String p1) throws ComponentLookupException;
    
    boolean hasComponent(final String p0);
    
    boolean hasComponent(final String p0, final String p1);
    
    void release(final Object p0) throws ComponentLifecycleException;
    
    void setClassWorld(final ClassWorld p0);
    
    void setConfiguration(final URL p0) throws IOException;
    
    void setConfiguration(final Reader p0) throws IOException;
    
    void addContextValue(final Object p0, final Object p1);
    
    void setProperties(final Properties p0);
    
    void setProperties(final File p0);
    
    void start(final ClassWorld p0) throws PlexusContainerException, PlexusConfigurationResourceException;
    
    void start() throws PlexusContainerException, PlexusConfigurationResourceException;
    
    void stop();
    
    void setLoggerManager(final LoggerManager p0);
}
