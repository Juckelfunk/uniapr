// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

public class LogEnablePhase extends AbstractPhase
{
    public void execute(final Object object, final ComponentManager componentManager) throws PhaseExecutionException {
        if (object instanceof LogEnabled) {
            final LoggerManager loggerManager = componentManager.getContainer().getLoggerManager();
            final ComponentDescriptor descriptor = componentManager.getComponentDescriptor();
            final Logger logger = loggerManager.getLoggerForComponent(descriptor.getRole(), descriptor.getRoleHint());
            ((LogEnabled)object).enableLogging(logger);
        }
    }
}
