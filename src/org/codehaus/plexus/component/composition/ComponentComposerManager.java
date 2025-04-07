// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.composition;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

public interface ComponentComposerManager
{
    public static final String ROLE = ((ComponentComposerManager$1.class$org$codehaus$plexus$component$composition$ComponentComposerManager == null) ? (ComponentComposerManager$1.class$org$codehaus$plexus$component$composition$ComponentComposerManager = ComponentComposerManager$1.class$("org.codehaus.plexus.component.composition.ComponentComposerManager")) : ComponentComposerManager$1.class$org$codehaus$plexus$component$composition$ComponentComposerManager).getName();
    
    void assembleComponent(final Object p0, final ComponentDescriptor p1, final PlexusContainer p2) throws CompositionException, UndefinedComponentComposerException;
}
