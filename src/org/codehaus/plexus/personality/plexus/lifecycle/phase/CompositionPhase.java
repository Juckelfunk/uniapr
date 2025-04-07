// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.composition.UndefinedComponentComposerException;
import org.codehaus.plexus.component.composition.CompositionException;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

public class CompositionPhase extends AbstractPhase
{
    public void execute(final Object object, final ComponentManager manager) throws PhaseExecutionException {
        final PlexusContainer container = manager.getContainer();
        final ComponentDescriptor descriptor = manager.getComponentDescriptor();
        try {
            container.composeComponent(object, descriptor);
        }
        catch (CompositionException e) {
            throw new PhaseExecutionException("Error composing component", e);
        }
        catch (UndefinedComponentComposerException e2) {
            throw new PhaseExecutionException("Error composing component", e2);
        }
    }
}
