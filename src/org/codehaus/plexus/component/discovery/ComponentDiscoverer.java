// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.discovery;

import org.codehaus.plexus.configuration.PlexusConfigurationException;
import java.util.List;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.context.Context;

public interface ComponentDiscoverer
{
    public static final String ROLE = ((ComponentDiscoverer$1.class$org$codehaus$plexus$component$discovery$ComponentDiscoverer == null) ? (ComponentDiscoverer$1.class$org$codehaus$plexus$component$discovery$ComponentDiscoverer = ComponentDiscoverer$1.class$("org.codehaus.plexus.component.discovery.ComponentDiscoverer")) : ComponentDiscoverer$1.class$org$codehaus$plexus$component$discovery$ComponentDiscoverer).getName();
    
    void setManager(final ComponentDiscovererManager p0);
    
    List findComponents(final Context p0, final ClassRealm p1) throws PlexusConfigurationException;
}
