// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.repository;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.component.repository.exception.ComponentImplementationNotFoundException;
import java.util.List;
import java.util.Map;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.configuration.PlexusConfiguration;

public interface ComponentRepository
{
    void configure(final PlexusConfiguration p0);
    
    void initialize() throws ComponentRepositoryException;
    
    boolean hasComponent(final String p0);
    
    boolean hasComponent(final String p0, final String p1);
    
    void addComponentDescriptor(final ComponentDescriptor p0) throws ComponentRepositoryException;
    
    void addComponentDescriptor(final PlexusConfiguration p0) throws ComponentRepositoryException;
    
    ComponentDescriptor getComponentDescriptor(final String p0);
    
    Map getComponentDescriptorMap(final String p0);
    
    List getComponentDependencies(final ComponentDescriptor p0);
    
    void validateComponentDescriptor(final ComponentDescriptor p0) throws ComponentImplementationNotFoundException;
    
    void setClassRealm(final ClassRealm p0);
}
