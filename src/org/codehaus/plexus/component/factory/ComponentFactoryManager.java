// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.factory;

public interface ComponentFactoryManager
{
    ComponentFactory findComponentFactory(final String p0) throws UndefinedComponentFactoryException;
    
    ComponentFactory getDefaultComponentFactory() throws UndefinedComponentFactoryException;
}
