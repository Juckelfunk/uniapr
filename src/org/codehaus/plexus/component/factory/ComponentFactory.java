// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.factory;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

public interface ComponentFactory
{
    public static final String ROLE = ((ComponentFactory$1.class$org$codehaus$plexus$component$factory$ComponentFactory == null) ? (ComponentFactory$1.class$org$codehaus$plexus$component$factory$ComponentFactory = ComponentFactory$1.class$("org.codehaus.plexus.component.factory.ComponentFactory")) : ComponentFactory$1.class$org$codehaus$plexus$component$factory$ComponentFactory).getName();
    
    String getId();
    
    Object newInstance(final ComponentDescriptor p0, final ClassRealm p1, final PlexusContainer p2) throws ComponentInstantiationException;
}
