// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

public class ContextualizePhase extends AbstractPhase
{
    public void execute(final Object object, final ComponentManager manager) throws PhaseExecutionException {
        if (object instanceof Contextualizable) {
            final Context context = manager.getContainer().getContext();
            try {
                ((Contextualizable)object).contextualize(context);
            }
            catch (ContextException e) {
                throw new PhaseExecutionException("Unable to contextualize component", e);
            }
        }
    }
}
