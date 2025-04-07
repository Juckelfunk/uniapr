// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.composition;

import java.util.List;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.logging.AbstractLogEnabled;

public abstract class AbstractComponentComposer extends AbstractLogEnabled implements ComponentComposer
{
    private String id;
    
    public String getId() {
        return this.id;
    }
}
