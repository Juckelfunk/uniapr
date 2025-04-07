// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.repository;

import org.codehaus.plexus.component.composition.ComponentComposer;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.component.factory.ComponentFactory;

public class ComponentProfile
{
    private ComponentFactory componentFactory;
    private LifecycleHandler lifecycleHandler;
    private ComponentManager componentManager;
    private ComponentComposer componentComposer;
    
    public ComponentFactory getComponentFactory() {
        return this.componentFactory;
    }
    
    public void setComponentFactory(final ComponentFactory componentFactory) {
        this.componentFactory = componentFactory;
    }
    
    public LifecycleHandler getLifecycleHandler() {
        return this.lifecycleHandler;
    }
    
    public void setLifecycleHandler(final LifecycleHandler lifecycleHandler) {
        this.lifecycleHandler = lifecycleHandler;
    }
    
    public ComponentManager getComponentManager() {
        return this.componentManager;
    }
    
    public void setComponentManager(final ComponentManager componentManager) {
        this.componentManager = componentManager;
    }
    
    public ComponentComposer getComponentComposer() {
        return this.componentComposer;
    }
    
    public void setComponentComposer(final ComponentComposer componentComposer) {
        this.componentComposer = componentComposer;
    }
}
