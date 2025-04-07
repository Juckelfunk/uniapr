// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

public class AutoConfigurePhase extends AbstractPhase
{
    public static final String DEFAULT_CONFIGURATOR_ID = "basic";
    
    public void execute(final Object object, final ComponentManager manager) throws PhaseExecutionException {
        try {
            final ComponentDescriptor descriptor = manager.getComponentDescriptor();
            String configuratorId = descriptor.getComponentConfigurator();
            if (StringUtils.isEmpty(configuratorId)) {
                configuratorId = "basic";
            }
            final ComponentConfigurator componentConfigurator = (ComponentConfigurator)manager.getContainer().lookup(ComponentConfigurator.ROLE, configuratorId);
            if (manager.getComponentDescriptor().hasConfiguration()) {
                componentConfigurator.configureComponent(object, manager.getComponentDescriptor().getConfiguration(), manager.getContainer().getContainerRealm());
            }
        }
        catch (ComponentLookupException e) {
            throw new PhaseExecutionException("Unable to auto-configure component as its configurator could not be found", e);
        }
        catch (ComponentConfigurationException e2) {
            throw new PhaseExecutionException("Unable to auto-configure component", e2);
        }
    }
}
