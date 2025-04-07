// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.discovery;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import java.util.Arrays;
import java.util.Enumeration;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.configuration.PlexusConfigurationMerger;
import org.codehaus.plexus.component.repository.io.PlexusTools;
import java.util.Map;
import java.io.Reader;
import org.codehaus.plexus.util.InterpolationFilterReader;
import org.codehaus.plexus.context.ContextMapAdapter;
import java.io.InputStreamReader;
import java.net.URL;
import java.io.IOException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.context.Context;

public class PlexusXmlComponentDiscoverer implements ComponentDiscoverer
{
    private static final String PLEXUS_XML_RESOURCE = "META-INF/plexus/plexus.xml";
    private ComponentDiscovererManager manager;
    
    public void setManager(final ComponentDiscovererManager manager) {
        this.manager = manager;
    }
    
    public List findComponents(final Context context, final ClassRealm classRealm) throws PlexusConfigurationException {
        final PlexusConfiguration configuration = this.discoverConfiguration(context, classRealm);
        final List componentSetDescriptors = new ArrayList();
        final ComponentSetDescriptor componentSetDescriptor = this.createComponentDescriptors(configuration, classRealm);
        componentSetDescriptors.add(componentSetDescriptor);
        final ComponentDiscoveryEvent event = new ComponentDiscoveryEvent(componentSetDescriptor);
        this.manager.fireComponentDiscoveryEvent(event);
        return componentSetDescriptors;
    }
    
    public PlexusConfiguration discoverConfiguration(final Context context, final ClassRealm classRealm) throws PlexusConfigurationException {
        PlexusConfiguration configuration = null;
        Enumeration resources = null;
        try {
            resources = classRealm.findResources("META-INF/plexus/plexus.xml");
        }
        catch (IOException e) {
            throw new PlexusConfigurationException("Error retrieving configuration resources: META-INF/plexus/plexus.xml from class realm: " + classRealm.getId(), e);
        }
        final Enumeration e2 = resources;
        while (e2.hasMoreElements()) {
            final URL url = e2.nextElement();
            InputStreamReader reader = null;
            try {
                reader = new InputStreamReader(url.openStream());
                final ContextMapAdapter contextAdapter = new ContextMapAdapter(context);
                final InterpolationFilterReader interpolationFilterReader = new InterpolationFilterReader(reader, contextAdapter);
                final PlexusConfiguration discoveredConfig = PlexusTools.buildConfiguration(url.toExternalForm(), interpolationFilterReader);
                if (configuration == null) {
                    configuration = discoveredConfig;
                }
                else {
                    configuration = PlexusConfigurationMerger.merge(configuration, discoveredConfig);
                }
            }
            catch (IOException ex) {
                throw new PlexusConfigurationException("Error reading configuration from: " + url.toExternalForm(), ex);
            }
            finally {
                IOUtil.close(reader);
            }
        }
        return configuration;
    }
    
    private ComponentSetDescriptor createComponentDescriptors(final PlexusConfiguration configuration, final ClassRealm classRealm) throws PlexusConfigurationException {
        final ComponentSetDescriptor componentSetDescriptor = new ComponentSetDescriptor();
        if (configuration != null) {
            final List componentDescriptors = new ArrayList();
            final PlexusConfiguration[] componentConfigurations = configuration.getChild("components").getChildren("component");
            for (int i = 0; i < componentConfigurations.length; ++i) {
                final PlexusConfiguration componentConfiguration = componentConfigurations[i];
                ComponentDescriptor componentDescriptor = null;
                try {
                    componentDescriptor = PlexusTools.buildComponentDescriptor(componentConfiguration);
                }
                catch (PlexusConfigurationException e) {
                    throw new PlexusConfigurationException("Cannot build component descriptor from resource found in:\n" + Arrays.asList(classRealm.getConstituents()), e);
                }
                componentDescriptor.setComponentType("plexus");
                componentDescriptors.add(componentDescriptor);
            }
            componentSetDescriptor.setComponents(componentDescriptors);
        }
        return componentSetDescriptor;
    }
}
