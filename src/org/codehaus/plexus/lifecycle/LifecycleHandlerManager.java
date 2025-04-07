// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.lifecycle;

public interface LifecycleHandlerManager
{
    void initialize();
    
    LifecycleHandler getDefaultLifecycleHandler() throws UndefinedLifecycleHandlerException;
    
    LifecycleHandler getLifecycleHandler(final String p0) throws UndefinedLifecycleHandlerException;
}
