// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

public class ConfigurablePhase extends AbstractPhase
{
    public void execute(final Object object, final ComponentManager manager) throws PhaseExecutionException {
        if (object instanceof Configurable) {
            try {
                ((Configurable)object).configure(manager.getComponentDescriptor().getConfiguration());
            }
            catch (PlexusConfigurationException e) {
                throw new PhaseExecutionException("Error occurred during phase execution", e);
            }
        }
    }
}
