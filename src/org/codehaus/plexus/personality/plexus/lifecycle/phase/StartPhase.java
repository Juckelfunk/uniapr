// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

public class StartPhase extends AbstractPhase
{
    public void execute(final Object object, final ComponentManager manager) throws PhaseExecutionException {
        if (object instanceof Startable) {
            try {
                ((Startable)object).start();
            }
            catch (StartingException e) {
                throw new PhaseExecutionException("Error starting component", e);
            }
        }
    }
}
