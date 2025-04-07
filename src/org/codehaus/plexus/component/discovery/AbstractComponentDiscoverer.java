// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.discovery;

import java.net.URLConnection;
import java.util.Enumeration;
import org.codehaus.plexus.util.IOUtil;
import java.util.Map;
import org.codehaus.plexus.util.InterpolationFilterReader;
import org.codehaus.plexus.context.ContextMapAdapter;
import java.io.InputStreamReader;
import java.net.URL;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import java.io.Reader;

public abstract class AbstractComponentDiscoverer implements ComponentDiscoverer
{
    private ComponentDiscovererManager manager;
    
    protected abstract String getComponentDescriptorLocation();
    
    protected abstract ComponentSetDescriptor createComponentDescriptors(final Reader p0, final String p1) throws PlexusConfigurationException;
    
    public void setManager(final ComponentDiscovererManager manager) {
        this.manager = manager;
    }
    
    public List findComponents(final Context context, final ClassRealm classRealm) throws PlexusConfigurationException {
        final List componentSetDescriptors = new ArrayList();
        Enumeration resources;
        try {
            resources = classRealm.findResources(this.getComponentDescriptorLocation());
        }
        catch (IOException e2) {
            throw new PlexusConfigurationException("Unable to retrieve resources for: " + this.getComponentDescriptorLocation() + " in class realm: " + classRealm.getId());
        }
        final Enumeration e = resources;
        while (e.hasMoreElements()) {
            final URL url = e.nextElement();
            InputStreamReader reader = null;
            try {
                final URLConnection conn = url.openConnection();
                conn.setUseCaches(false);
                conn.connect();
                reader = new InputStreamReader(conn.getInputStream());
                final InterpolationFilterReader input = new InterpolationFilterReader(reader, new ContextMapAdapter(context));
                final ComponentSetDescriptor componentSetDescriptor = this.createComponentDescriptors(input, url.toString());
                componentSetDescriptors.add(componentSetDescriptor);
                final ComponentDiscoveryEvent event = new ComponentDiscoveryEvent(componentSetDescriptor);
                this.manager.fireComponentDiscoveryEvent(event);
            }
            catch (IOException ex) {
                throw new PlexusConfigurationException("Error reading configuration " + url, ex);
            }
            finally {
                IOUtil.close(reader);
            }
        }
        return componentSetDescriptors;
    }
}
