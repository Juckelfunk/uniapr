// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import java.util.List;
import java.util.Map;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.PlexusContainer;

public class PlexusContainerLocator implements ServiceLocator
{
    private PlexusContainer container;
    
    public PlexusContainerLocator(final PlexusContainer container) {
        this.container = container;
    }
    
    public Object lookup(final String componentKey) throws ComponentLookupException {
        return this.container.lookup(componentKey);
    }
    
    public Object lookup(final String role, final String roleHint) throws ComponentLookupException {
        return this.container.lookup(role, roleHint);
    }
    
    public Map lookupMap(final String role) throws ComponentLookupException {
        return this.container.lookupMap(role);
    }
    
    public List lookupList(final String role) throws ComponentLookupException {
        return this.container.lookupList(role);
    }
    
    public void release(final Object component) throws ComponentLifecycleException {
        this.container.release(component);
    }
    
    public void releaseAll(final Map components) throws ComponentLifecycleException {
        this.container.releaseAll(components);
    }
    
    public void releaseAll(final List components) throws ComponentLifecycleException {
        this.container.releaseAll(components);
    }
    
    public boolean hasComponent(final String componentKey) {
        return this.container.hasComponent(componentKey);
    }
    
    public boolean hasComponent(final String role, final String roleHint) {
        return this.container.hasComponent(role, roleHint);
    }
}
