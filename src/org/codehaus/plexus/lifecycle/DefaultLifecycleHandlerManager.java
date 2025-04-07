// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.lifecycle;

import java.util.Iterator;
import java.util.List;

public class DefaultLifecycleHandlerManager implements LifecycleHandlerManager
{
    private List lifecycleHandlers;
    private String defaultLifecycleHandlerId;
    
    public DefaultLifecycleHandlerManager() {
        this.lifecycleHandlers = null;
        this.defaultLifecycleHandlerId = "plexus";
    }
    
    public void initialize() {
        for (final LifecycleHandler lifecycleHandler : this.lifecycleHandlers) {
            lifecycleHandler.initialize();
        }
    }
    
    public LifecycleHandler getLifecycleHandler(final String id) throws UndefinedLifecycleHandlerException {
        LifecycleHandler lifecycleHandler = null;
        final Iterator iterator = this.lifecycleHandlers.iterator();
        while (iterator.hasNext()) {
            lifecycleHandler = iterator.next();
            if (id.equals(lifecycleHandler.getId())) {
                return lifecycleHandler;
            }
        }
        throw new UndefinedLifecycleHandlerException("Specified lifecycle handler cannot be found: " + id);
    }
    
    public LifecycleHandler getDefaultLifecycleHandler() throws UndefinedLifecycleHandlerException {
        return this.getLifecycleHandler(this.defaultLifecycleHandlerId);
    }
}
