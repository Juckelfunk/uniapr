// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.configuration.PlexusConfiguration;

public interface Configurable
{
    void configure(final PlexusConfiguration p0) throws PlexusConfigurationException;
}
