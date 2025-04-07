// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component;

import java.util.Map;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.repository.ComponentRequirement;

public interface MapOrientedComponent
{
    void addComponentRequirement(final ComponentRequirement p0, final Object p1) throws ComponentConfigurationException;
    
    void setComponentConfiguration(final Map p0) throws ComponentConfigurationException;
}
