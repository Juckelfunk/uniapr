// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.manager;

import java.util.Map;
import org.codehaus.plexus.lifecycle.UndefinedLifecycleHandlerException;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.lifecycle.LifecycleHandlerManager;

public interface ComponentManagerManager
{
    public static final String ROLE = ((ComponentManagerManager$1.class$org$codehaus$plexus$component$manager$ComponentManagerManager == null) ? (ComponentManagerManager$1.class$org$codehaus$plexus$component$manager$ComponentManagerManager = ComponentManagerManager$1.class$("org.codehaus.plexus.component.manager.ComponentManagerManager")) : ComponentManagerManager$1.class$org$codehaus$plexus$component$manager$ComponentManagerManager).getName();
    
    void setLifecycleHandlerManager(final LifecycleHandlerManager p0);
    
    ComponentManager findComponentManagerByComponentKey(final String p0);
    
    ComponentManager findComponentManagerByComponentInstance(final Object p0);
    
    ComponentManager createComponentManager(final ComponentDescriptor p0, final PlexusContainer p1) throws UndefinedComponentManagerException, UndefinedLifecycleHandlerException;
    
    Map getComponentManagers();
    
    void associateComponentWithComponentManager(final Object p0, final ComponentManager p1);
    
    void unassociateComponentWithComponentManager(final Object p0);
}
