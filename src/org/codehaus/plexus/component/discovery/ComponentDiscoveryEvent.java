// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.discovery;

import org.codehaus.plexus.component.repository.ComponentSetDescriptor;

public class ComponentDiscoveryEvent
{
    private ComponentSetDescriptor componentSetDescriptor;
    
    public ComponentDiscoveryEvent(final ComponentSetDescriptor componentSetDescriptor) {
        this.componentSetDescriptor = componentSetDescriptor;
    }
    
    public ComponentSetDescriptor getComponentSetDescriptor() {
        return this.componentSetDescriptor;
    }
}
