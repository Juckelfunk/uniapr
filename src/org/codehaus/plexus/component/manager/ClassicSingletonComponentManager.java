// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.manager;

import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;

public class ClassicSingletonComponentManager extends AbstractComponentManager
{
    private Object lock;
    private Object singleton;
    
    public ClassicSingletonComponentManager() {
        this.lock = new Object();
    }
    
    public void release(final Object component) throws ComponentLifecycleException {
        synchronized (this.lock) {
            if (this.singleton == component) {
                this.decrementConnectionCount();
                if (!this.connected()) {
                    this.dispose();
                }
            }
            else {
                this.getLogger().warn("Component returned which is not the same manager. Ignored. component=" + component);
            }
        }
    }
    
    public void dispose() throws ComponentLifecycleException {
        synchronized (this.lock) {
            if (this.singleton != null) {
                this.endComponentLifecycle(this.singleton);
                this.singleton = null;
            }
        }
    }
    
    public Object getComponent() throws ComponentInstantiationException, ComponentLifecycleException {
        synchronized (this.lock) {
            if (this.singleton == null) {
                this.singleton = this.createComponentInstance();
            }
            this.incrementConnectionCount();
            return this.singleton;
        }
    }
}
