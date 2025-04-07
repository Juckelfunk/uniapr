// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.manager;

import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.lifecycle.UndefinedLifecycleHandlerException;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import java.util.Iterator;
import java.util.Collections;
import java.util.HashMap;
import org.codehaus.plexus.lifecycle.LifecycleHandlerManager;
import java.util.List;
import java.util.Map;

public class DefaultComponentManagerManager implements ComponentManagerManager
{
    private Map activeComponentManagers;
    private List componentManagers;
    private String defaultComponentManagerId;
    private LifecycleHandlerManager lifecycleHandlerManager;
    private Map componentManagersByComponentHashCode;
    
    public DefaultComponentManagerManager() {
        this.activeComponentManagers = new HashMap();
        this.componentManagers = null;
        this.defaultComponentManagerId = null;
        this.componentManagersByComponentHashCode = Collections.synchronizedMap(new HashMap<Object, Object>());
    }
    
    public void setLifecycleHandlerManager(final LifecycleHandlerManager lifecycleHandlerManager) {
        this.lifecycleHandlerManager = lifecycleHandlerManager;
    }
    
    private ComponentManager copyComponentManager(final String id) throws UndefinedComponentManagerException {
        ComponentManager componentManager = null;
        final Iterator iterator = this.componentManagers.iterator();
        while (iterator.hasNext()) {
            componentManager = iterator.next();
            if (id.equals(componentManager.getId())) {
                return componentManager.copy();
            }
        }
        throw new UndefinedComponentManagerException("Specified component manager cannot be found: " + id);
    }
    
    public ComponentManager createComponentManager(final ComponentDescriptor descriptor, final PlexusContainer container) throws UndefinedComponentManagerException, UndefinedLifecycleHandlerException {
        String componentManagerId = descriptor.getInstantiationStrategy();
        if (componentManagerId == null) {
            componentManagerId = this.defaultComponentManagerId;
        }
        final ComponentManager componentManager = this.copyComponentManager(componentManagerId);
        componentManager.setup(container, this.findLifecycleHandler(descriptor), descriptor);
        componentManager.initialize();
        this.activeComponentManagers.put(descriptor.getComponentKey(), componentManager);
        return componentManager;
    }
    
    public ComponentManager findComponentManagerByComponentInstance(final Object component) {
        return this.componentManagersByComponentHashCode.get(new Integer(component.hashCode()));
    }
    
    public ComponentManager findComponentManagerByComponentKey(final String componentKey) {
        final ComponentManager componentManager = this.activeComponentManagers.get(componentKey);
        return componentManager;
    }
    
    private LifecycleHandler findLifecycleHandler(final ComponentDescriptor descriptor) throws UndefinedLifecycleHandlerException {
        final String lifecycleHandlerId = descriptor.getLifecycleHandler();
        LifecycleHandler lifecycleHandler;
        if (lifecycleHandlerId == null) {
            lifecycleHandler = this.lifecycleHandlerManager.getDefaultLifecycleHandler();
        }
        else {
            lifecycleHandler = this.lifecycleHandlerManager.getLifecycleHandler(lifecycleHandlerId);
        }
        return lifecycleHandler;
    }
    
    public Map getComponentManagers() {
        return this.activeComponentManagers;
    }
    
    public void associateComponentWithComponentManager(final Object component, final ComponentManager componentManager) {
        this.componentManagersByComponentHashCode.put(new Integer(component.hashCode()), componentManager);
    }
    
    public void unassociateComponentWithComponentManager(final Object component) {
        this.componentManagersByComponentHashCode.remove(new Integer(component.hashCode()));
    }
}
