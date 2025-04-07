// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.composition;

import java.util.List;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

public interface ComponentComposer
{
    public static final String ROLE = ((ComponentComposer$1.class$org$codehaus$plexus$component$composition$ComponentComposer == null) ? (ComponentComposer$1.class$org$codehaus$plexus$component$composition$ComponentComposer = ComponentComposer$1.class$("org.codehaus.plexus.component.composition.ComponentComposer")) : ComponentComposer$1.class$org$codehaus$plexus$component$composition$ComponentComposer).getName();
    
    String getId();
    
    List assembleComponent(final Object p0, final ComponentDescriptor p1, final PlexusContainer p2) throws CompositionException, UndefinedComponentComposerException;
}
