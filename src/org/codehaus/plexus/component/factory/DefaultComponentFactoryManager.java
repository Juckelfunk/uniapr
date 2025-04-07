// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.factory;

import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.component.factory.java.JavaComponentFactory;
import java.util.List;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

public class DefaultComponentFactoryManager implements ComponentFactoryManager, Contextualizable
{
    private String defaultComponentFactoryId;
    private ComponentFactory defaultComponentFactory;
    private PlexusContainer container;
    private List componentFactories;
    
    public DefaultComponentFactoryManager() {
        this.defaultComponentFactoryId = "java";
        this.defaultComponentFactory = new JavaComponentFactory();
    }
    
    public ComponentFactory findComponentFactory(final String id) throws UndefinedComponentFactoryException {
        if (StringUtils.isEmpty(id) || this.defaultComponentFactoryId.equals(id)) {
            return this.defaultComponentFactory;
        }
        try {
            return (ComponentFactory)this.container.lookup(ComponentFactory.ROLE, id);
        }
        catch (ComponentLookupException e) {
            throw new UndefinedComponentFactoryException("Specified component factory cannot be found: " + id, e);
        }
    }
    
    public ComponentFactory getDefaultComponentFactory() throws UndefinedComponentFactoryException {
        return this.defaultComponentFactory;
    }
    
    public void contextualize(final Context context) throws ContextException {
        this.container = (PlexusContainer)context.get("plexus");
    }
}
