// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus;

import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.classworlds.ClassRealm;
import java.io.File;
import org.codehaus.plexus.component.discovery.ComponentDiscoveryListener;
import org.codehaus.plexus.component.composition.UndefinedComponentComposerException;
import org.codehaus.plexus.component.composition.CompositionException;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.configuration.PlexusConfigurationResourceException;
import java.io.Reader;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import java.util.Map;
import java.util.List;
import java.util.Date;

public interface PlexusContainer
{
    public static final String ROLE = ((PlexusContainer$1.class$org$codehaus$plexus$PlexusContainer == null) ? (PlexusContainer$1.class$org$codehaus$plexus$PlexusContainer = PlexusContainer$1.class$("org.codehaus.plexus.PlexusContainer")) : PlexusContainer$1.class$org$codehaus$plexus$PlexusContainer).getName();
    
    Date getCreationDate();
    
    boolean hasChildContainer(final String p0);
    
    void removeChildContainer(final String p0);
    
    PlexusContainer getChildContainer(final String p0);
    
    PlexusContainer createChildContainer(final String p0, final List p1, final Map p2) throws PlexusContainerException;
    
    PlexusContainer createChildContainer(final String p0, final List p1, final Map p2, final List p3) throws PlexusContainerException;
    
    Object lookup(final String p0) throws ComponentLookupException;
    
    Object lookup(final String p0, final String p1) throws ComponentLookupException;
    
    Map lookupMap(final String p0) throws ComponentLookupException;
    
    List lookupList(final String p0) throws ComponentLookupException;
    
    ComponentDescriptor getComponentDescriptor(final String p0);
    
    Map getComponentDescriptorMap(final String p0);
    
    List getComponentDescriptorList(final String p0);
    
    void addComponentDescriptor(final ComponentDescriptor p0) throws ComponentRepositoryException;
    
    void release(final Object p0) throws ComponentLifecycleException;
    
    void releaseAll(final Map p0) throws ComponentLifecycleException;
    
    void releaseAll(final List p0) throws ComponentLifecycleException;
    
    boolean hasComponent(final String p0);
    
    boolean hasComponent(final String p0, final String p1);
    
    void suspend(final Object p0) throws ComponentLifecycleException;
    
    void resume(final Object p0) throws ComponentLifecycleException;
    
    void initialize() throws PlexusContainerException;
    
    boolean isInitialized();
    
    void start() throws PlexusContainerException;
    
    boolean isStarted();
    
    void dispose();
    
    Context getContext();
    
    void setParentPlexusContainer(final PlexusContainer p0);
    
    void addContextValue(final Object p0, final Object p1);
    
    void setConfigurationResource(final Reader p0) throws PlexusConfigurationResourceException;
    
    Logger getLogger();
    
    Object createComponentInstance(final ComponentDescriptor p0) throws ComponentInstantiationException, ComponentLifecycleException;
    
    void composeComponent(final Object p0, final ComponentDescriptor p1) throws CompositionException, UndefinedComponentComposerException;
    
    void registerComponentDiscoveryListener(final ComponentDiscoveryListener p0);
    
    void removeComponentDiscoveryListener(final ComponentDiscoveryListener p0);
    
    void addJarRepository(final File p0);
    
    void addJarResource(final File p0) throws PlexusContainerException;
    
    ClassRealm getContainerRealm();
    
    ClassRealm getComponentRealm(final String p0);
    
    void setLoggerManager(final LoggerManager p0);
    
    LoggerManager getLoggerManager();
}
