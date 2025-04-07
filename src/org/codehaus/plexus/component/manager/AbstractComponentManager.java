// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.manager;

import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.PlexusContainer;

public abstract class AbstractComponentManager implements ComponentManager, Cloneable
{
    private PlexusContainer container;
    private ComponentDescriptor componentDescriptor;
    private LifecycleHandler lifecycleHandler;
    private int connections;
    private String id;
    
    public AbstractComponentManager() {
        this.id = null;
    }
    
    public ComponentManager copy() {
        try {
            final ComponentManager componentManager = (ComponentManager)this.clone();
            return componentManager;
        }
        catch (CloneNotSupportedException e) {
            return null;
        }
    }
    
    public ComponentDescriptor getComponentDescriptor() {
        return this.componentDescriptor;
    }
    
    public String getId() {
        return this.id;
    }
    
    public LifecycleHandler getLifecycleHandler() {
        return this.lifecycleHandler;
    }
    
    protected void incrementConnectionCount() {
        ++this.connections;
    }
    
    protected void decrementConnectionCount() {
        --this.connections;
    }
    
    protected boolean connected() {
        return this.connections > 0;
    }
    
    public int getConnections() {
        return this.connections;
    }
    
    public void setup(final PlexusContainer container, final LifecycleHandler lifecycleHandler, final ComponentDescriptor componentDescriptor) {
        this.container = container;
        this.lifecycleHandler = lifecycleHandler;
        this.componentDescriptor = componentDescriptor;
    }
    
    public void initialize() {
    }
    
    protected Object createComponentInstance() throws ComponentInstantiationException, ComponentLifecycleException {
        final Object component = this.container.createComponentInstance(this.componentDescriptor);
        this.startComponentLifecycle(component);
        return component;
    }
    
    protected void startComponentLifecycle(final Object component) throws ComponentLifecycleException {
        try {
            this.getLifecycleHandler().start(component, this);
        }
        catch (PhaseExecutionException e) {
            throw new ComponentLifecycleException("Error starting component", e);
        }
    }
    
    public void suspend(final Object component) throws ComponentLifecycleException {
        try {
            this.getLifecycleHandler().suspend(component, this);
        }
        catch (PhaseExecutionException e) {
            throw new ComponentLifecycleException("Error suspending component", e);
        }
    }
    
    public void resume(final Object component) throws ComponentLifecycleException {
        try {
            this.getLifecycleHandler().resume(component, this);
        }
        catch (PhaseExecutionException e) {
            throw new ComponentLifecycleException("Error suspending component", e);
        }
    }
    
    protected void endComponentLifecycle(final Object component) throws ComponentLifecycleException {
        try {
            this.getLifecycleHandler().end(component, this);
        }
        catch (PhaseExecutionException e) {
            throw new ComponentLifecycleException("Error ending component lifecycle", e);
        }
    }
    
    public PlexusContainer getContainer() {
        return this.container;
    }
    
    public Logger getLogger() {
        return this.container.getLogger();
    }
}
