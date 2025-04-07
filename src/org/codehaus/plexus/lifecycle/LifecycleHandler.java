// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.lifecycle;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;
import org.codehaus.plexus.component.manager.ComponentManager;

public interface LifecycleHandler
{
    String getId();
    
    void start(final Object p0, final ComponentManager p1) throws PhaseExecutionException;
    
    void suspend(final Object p0, final ComponentManager p1) throws PhaseExecutionException;
    
    void resume(final Object p0, final ComponentManager p1) throws PhaseExecutionException;
    
    void end(final Object p0, final ComponentManager p1) throws PhaseExecutionException;
    
    void initialize();
}
