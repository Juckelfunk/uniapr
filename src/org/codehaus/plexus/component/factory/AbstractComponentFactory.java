// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.factory;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

public abstract class AbstractComponentFactory implements ComponentFactory
{
    protected String id;
    
    public String getId() {
        return this.id;
    }
}
