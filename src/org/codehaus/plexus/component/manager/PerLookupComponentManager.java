// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.manager;

import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;

public class PerLookupComponentManager extends AbstractComponentManager
{
    public void dispose() {
    }
    
    public Object getComponent() throws ComponentInstantiationException, ComponentLifecycleException {
        final Object component = this.createComponentInstance();
        return component;
    }
    
    public void release(final Object component) throws ComponentLifecycleException {
        this.endComponentLifecycle(component);
    }
}
