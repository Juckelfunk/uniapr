// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.manager;

import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.PlexusContainer;

public interface ComponentManager
{
    public static final String ROLE = ((ComponentManager$1.class$org$codehaus$plexus$component$manager$ComponentManager == null) ? (ComponentManager$1.class$org$codehaus$plexus$component$manager$ComponentManager = ComponentManager$1.class$("org.codehaus.plexus.component.manager.ComponentManager")) : ComponentManager$1.class$org$codehaus$plexus$component$manager$ComponentManager).getName();
    
    ComponentManager copy();
    
    String getId();
    
    void setup(final PlexusContainer p0, final LifecycleHandler p1, final ComponentDescriptor p2);
    
    void initialize();
    
    int getConnections();
    
    LifecycleHandler getLifecycleHandler();
    
    void dispose() throws ComponentLifecycleException;
    
    void release(final Object p0) throws ComponentLifecycleException;
    
    void suspend(final Object p0) throws ComponentLifecycleException;
    
    void resume(final Object p0) throws ComponentLifecycleException;
    
    Object getComponent() throws ComponentInstantiationException, ComponentLifecycleException;
    
    ComponentDescriptor getComponentDescriptor();
    
    PlexusContainer getContainer();
}
