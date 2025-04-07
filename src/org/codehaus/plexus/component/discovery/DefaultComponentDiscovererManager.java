// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.discovery;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class DefaultComponentDiscovererManager implements ComponentDiscovererManager
{
    private List componentDiscoverers;
    private List componentDiscoveryListeners;
    private List listeners;
    
    public List getComponentDiscoverers() {
        return this.componentDiscoverers;
    }
    
    public void registerComponentDiscoveryListener(final ComponentDiscoveryListener listener) {
        if (this.componentDiscoveryListeners == null) {
            this.componentDiscoveryListeners = new ArrayList();
        }
        this.componentDiscoveryListeners.add(listener);
    }
    
    public void removeComponentDiscoveryListener(final ComponentDiscoveryListener listener) {
        if (this.componentDiscoveryListeners != null) {
            this.componentDiscoveryListeners.remove(listener);
        }
    }
    
    public void fireComponentDiscoveryEvent(final ComponentDiscoveryEvent event) {
        if (this.componentDiscoveryListeners != null) {
            for (final ComponentDiscoveryListener listener : this.componentDiscoveryListeners) {
                listener.componentDiscovered(event);
            }
        }
    }
    
    public List getListenerDescriptors() {
        return this.listeners;
    }
    
    public void initialize() {
        for (final ComponentDiscoverer componentDiscoverer : this.componentDiscoverers) {
            componentDiscoverer.setManager(this);
        }
    }
}
