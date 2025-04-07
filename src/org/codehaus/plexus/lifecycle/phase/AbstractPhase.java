// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.lifecycle.phase;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;
import org.codehaus.plexus.component.manager.ComponentManager;

public abstract class AbstractPhase implements Phase
{
    public abstract void execute(final Object p0, final ComponentManager p1) throws PhaseExecutionException;
}
