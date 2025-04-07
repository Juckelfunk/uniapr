// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.composition;

import java.util.Collections;
import java.util.List;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

public class NoOpComponentComposer extends AbstractComponentComposer
{
    public String getId() {
        return null;
    }
    
    public List assembleComponent(final Object component, final ComponentDescriptor componentDescriptor, final PlexusContainer container) {
        return Collections.EMPTY_LIST;
    }
}
