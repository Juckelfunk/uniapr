// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

public class DisposePhase extends AbstractPhase
{
    public void execute(final Object object, final ComponentManager manager) {
        if (object instanceof Disposable) {
            ((Disposable)object).dispose();
        }
    }
}
