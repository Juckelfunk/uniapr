// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.discovery;

import java.util.List;

public interface ComponentDiscovererManager
{
    List getComponentDiscoverers();
    
    void registerComponentDiscoveryListener(final ComponentDiscoveryListener p0);
    
    void removeComponentDiscoveryListener(final ComponentDiscoveryListener p0);
    
    void fireComponentDiscoveryEvent(final ComponentDiscoveryEvent p0);
    
    void initialize();
    
    List getListenerDescriptors();
}
