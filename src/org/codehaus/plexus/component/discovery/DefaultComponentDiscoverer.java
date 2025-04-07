// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.discovery;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import java.util.List;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import java.util.ArrayList;
import org.codehaus.plexus.component.repository.io.PlexusTools;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import java.io.Reader;

public class DefaultComponentDiscoverer extends AbstractComponentDiscoverer
{
    public String getComponentDescriptorLocation() {
        return "META-INF/plexus/components.xml";
    }
    
    public ComponentSetDescriptor createComponentDescriptors(final Reader componentDescriptorReader, final String source) throws PlexusConfigurationException {
        final PlexusConfiguration componentDescriptorConfiguration = PlexusTools.buildConfiguration(source, componentDescriptorReader);
        final ComponentSetDescriptor componentSetDescriptor = new ComponentSetDescriptor();
        final List componentDescriptors = new ArrayList();
        final PlexusConfiguration[] componentConfigurations = componentDescriptorConfiguration.getChild("components").getChildren("component");
        for (int i = 0; i < componentConfigurations.length; ++i) {
            final PlexusConfiguration componentConfiguration = componentConfigurations[i];
            ComponentDescriptor componentDescriptor = null;
            try {
                componentDescriptor = PlexusTools.buildComponentDescriptor(componentConfiguration);
            }
            catch (PlexusConfigurationException e) {
                throw new PlexusConfigurationException("Cannot process component descriptor: " + source, e);
            }
            componentDescriptor.setComponentType("plexus");
            componentDescriptors.add(componentDescriptor);
        }
        componentSetDescriptor.setComponents(componentDescriptors);
        return componentSetDescriptor;
    }
}
