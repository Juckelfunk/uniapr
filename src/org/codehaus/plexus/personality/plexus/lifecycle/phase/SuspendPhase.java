// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

public class SuspendPhase extends AbstractPhase
{
    public void execute(final Object object, final ComponentManager manager) {
        if (object instanceof Suspendable) {
            ((Suspendable)object).suspend();
        }
    }
}
