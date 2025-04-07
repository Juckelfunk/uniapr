// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

public class LogDisablePhase extends AbstractPhase
{
    public void execute(final Object object, final ComponentManager componentManager) throws PhaseExecutionException {
        if (object instanceof LogEnabled) {
            LoggerManager loggerManager;
            try {
                loggerManager = (LoggerManager)componentManager.getContainer().lookup(LoggerManager.ROLE);
            }
            catch (ComponentLookupException e) {
                throw new PhaseExecutionException("Unable to locate logger manager", e);
            }
            final ComponentDescriptor descriptor = componentManager.getComponentDescriptor();
            loggerManager.returnComponentLogger(descriptor.getRole(), descriptor.getRoleHint());
        }
    }
}
