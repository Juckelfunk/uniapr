// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus;

import org.codehaus.plexus.component.composition.UndefinedComponentComposerException;
import org.codehaus.plexus.component.composition.CompositionException;
import org.codehaus.plexus.component.factory.ComponentFactory;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.component.factory.UndefinedComponentFactoryException;
import org.codehaus.plexus.logging.Logger;
import java.net.MalformedURLException;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.component.configurator.BasicComponentConfigurator;
import org.codehaus.plexus.util.IOUtil;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.InterpolationFilterReader;
import org.codehaus.plexus.context.ContextMapAdapter;
import java.io.InputStream;
import org.codehaus.plexus.configuration.processor.DirectoryConfigurationResourceHandler;
import org.codehaus.plexus.configuration.processor.ConfigurationResourceHandler;
import org.codehaus.plexus.configuration.processor.FileConfigurationResourceHandler;
import org.codehaus.plexus.configuration.processor.ConfigurationProcessor;
import org.codehaus.plexus.configuration.PlexusConfigurationMerger;
import org.codehaus.plexus.component.discovery.PlexusXmlComponentDiscoverer;
import org.codehaus.plexus.component.repository.io.PlexusTools;
import java.io.InputStreamReader;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.configuration.PlexusConfigurationResourceException;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.component.discovery.ComponentDiscoverer;
import org.codehaus.plexus.component.discovery.DiscoveryListenerDescriptor;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.configuration.processor.ConfigurationResourceNotFoundException;
import org.codehaus.plexus.configuration.processor.ConfigurationProcessingException;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import org.codehaus.plexus.lifecycle.UndefinedLifecycleHandlerException;
import org.codehaus.plexus.component.manager.UndefinedComponentManagerException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import java.util.Iterator;
import org.codehaus.plexus.component.discovery.ComponentDiscoveryListener;
import java.io.File;
import org.codehaus.classworlds.NoSuchRealmException;
import org.codehaus.classworlds.DuplicateRealmException;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;
import java.util.Date;
import java.util.Map;
import org.codehaus.plexus.component.composition.ComponentComposerManager;
import org.codehaus.plexus.component.factory.ComponentFactoryManager;
import org.codehaus.plexus.component.discovery.ComponentDiscovererManager;
import org.codehaus.plexus.lifecycle.LifecycleHandlerManager;
import org.codehaus.plexus.component.manager.ComponentManagerManager;
import org.codehaus.plexus.component.repository.ComponentRepository;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;
import java.io.Reader;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.context.DefaultContext;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.logging.AbstractLogEnabled;

public class DefaultPlexusContainer extends AbstractLogEnabled implements PlexusContainer
{
    private PlexusContainer parentContainer;
    private LoggerManager loggerManager;
    private DefaultContext context;
    protected PlexusConfiguration configuration;
    private Reader configurationReader;
    private ClassWorld classWorld;
    private ClassRealm coreRealm;
    private ClassRealm plexusRealm;
    private String name;
    private ComponentRepository componentRepository;
    private ComponentManagerManager componentManagerManager;
    private LifecycleHandlerManager lifecycleHandlerManager;
    private ComponentDiscovererManager componentDiscovererManager;
    private ComponentFactoryManager componentFactoryManager;
    private ComponentComposerManager componentComposerManager;
    private Map childContainers;
    public static final String BOOTSTRAP_CONFIGURATION = "org/codehaus/plexus/plexus-bootstrap.xml";
    private boolean started;
    private boolean initialized;
    private final Date creationDate;
    
    public DefaultPlexusContainer() {
        this.childContainers = new WeakHashMap();
        this.started = false;
        this.initialized = false;
        this.creationDate = new Date();
        this.context = new DefaultContext();
    }
    
    public Date getCreationDate() {
        return this.creationDate;
    }
    
    public boolean hasChildContainer(final String name) {
        return this.childContainers.get(name) != null;
    }
    
    public void removeChildContainer(final String name) {
        this.childContainers.remove(name);
    }
    
    public PlexusContainer getChildContainer(final String name) {
        return this.childContainers.get(name);
    }
    
    public PlexusContainer createChildContainer(final String name, final List classpathJars, final Map context) throws PlexusContainerException {
        return this.createChildContainer(name, classpathJars, context, Collections.EMPTY_LIST);
    }
    
    public PlexusContainer createChildContainer(final String name, final List classpathJars, final Map context, final List discoveryListeners) throws PlexusContainerException {
        if (this.hasChildContainer(name)) {
            throw new DuplicateChildContainerException(this.getName(), name);
        }
        final DefaultPlexusContainer child = new DefaultPlexusContainer();
        child.classWorld = this.classWorld;
        ClassRealm childRealm = null;
        final String childRealmId = this.getName() + ".child-container[" + name + "]";
        try {
            childRealm = this.classWorld.getRealm(childRealmId);
        }
        catch (NoSuchRealmException e) {
            try {
                childRealm = this.classWorld.newRealm(childRealmId);
            }
            catch (DuplicateRealmException impossibleError) {
                this.getLogger().error("An impossible error has occurred. After getRealm() failed, newRealm() produced duplication error on same id!", impossibleError);
            }
        }
        childRealm.setParent(this.plexusRealm);
        child.coreRealm = childRealm;
        child.plexusRealm = childRealm;
        child.setName(name);
        child.setParentPlexusContainer(this);
        child.setLoggerManager(this.loggerManager);
        for (final Map.Entry entry : context.entrySet()) {
            child.addContextValue(entry.getKey(), entry.getValue());
        }
        child.initialize();
        for (final Object next : classpathJars) {
            final File jar = (File)next;
            child.addJarResource(jar);
        }
        for (final ComponentDiscoveryListener listener : discoveryListeners) {
            child.registerComponentDiscoveryListener(listener);
        }
        child.start();
        this.childContainers.put(name, child);
        return child;
    }
    
    public Object lookup(final String componentKey) throws ComponentLookupException {
        Object component = null;
        ComponentManager componentManager = this.componentManagerManager.findComponentManagerByComponentKey(componentKey);
        if (componentManager == null) {
            final ComponentDescriptor descriptor = this.componentRepository.getComponentDescriptor(componentKey);
            if (descriptor == null) {
                if (this.parentContainer != null) {
                    return this.parentContainer.lookup(componentKey);
                }
                if (this.getLogger().isDebugEnabled()) {
                    this.getLogger().debug("Nonexistent component: " + componentKey);
                }
                final String message = "Component descriptor cannot be found in the component repository: " + componentKey + ".";
                throw new ComponentLookupException(message);
            }
            else {
                componentManager = this.createComponentManager(descriptor);
            }
        }
        try {
            component = componentManager.getComponent();
        }
        catch (ComponentInstantiationException e) {
            throw new ComponentLookupException("Unable to lookup component '" + componentKey + "', it could not be created", e);
        }
        catch (ComponentLifecycleException e2) {
            throw new ComponentLookupException("Unable to lookup component '" + componentKey + "', it could not be started", e2);
        }
        this.componentManagerManager.associateComponentWithComponentManager(component, componentManager);
        return component;
    }
    
    private ComponentManager createComponentManager(final ComponentDescriptor descriptor) throws ComponentLookupException {
        ComponentManager componentManager;
        try {
            componentManager = this.componentManagerManager.createComponentManager(descriptor, this);
        }
        catch (UndefinedComponentManagerException e) {
            final String message = "Cannot create component manager for " + descriptor.getComponentKey() + ", so we cannot provide a component instance.";
            throw new ComponentLookupException(message, e);
        }
        catch (UndefinedLifecycleHandlerException e2) {
            final String message = "Cannot create component manager for " + descriptor.getComponentKey() + ", so we cannot provide a component instance.";
            throw new ComponentLookupException(message, e2);
        }
        return componentManager;
    }
    
    public Map lookupMap(final String role) throws ComponentLookupException {
        final Map components = new HashMap();
        final Map componentDescriptors = this.getComponentDescriptorMap(role);
        if (componentDescriptors != null) {
            for (final String roleHint : componentDescriptors.keySet()) {
                final Object component = this.lookup(role, roleHint);
                components.put(roleHint, component);
            }
        }
        return components;
    }
    
    public List lookupList(final String role) throws ComponentLookupException {
        final List components = new ArrayList();
        final List componentDescriptors = this.getComponentDescriptorList(role);
        if (componentDescriptors != null) {
            for (final ComponentDescriptor descriptor : componentDescriptors) {
                final String roleHint = descriptor.getRoleHint();
                Object component;
                if (roleHint != null) {
                    component = this.lookup(role, roleHint);
                }
                else {
                    component = this.lookup(role);
                }
                components.add(component);
            }
        }
        return components;
    }
    
    public Object lookup(final String role, final String roleHint) throws ComponentLookupException {
        return this.lookup(role + roleHint);
    }
    
    public ComponentDescriptor getComponentDescriptor(final String componentKey) {
        ComponentDescriptor result = this.componentRepository.getComponentDescriptor(componentKey);
        if (result == null && this.parentContainer != null) {
            result = this.parentContainer.getComponentDescriptor(componentKey);
        }
        return result;
    }
    
    public Map getComponentDescriptorMap(final String role) {
        Map result = null;
        if (this.parentContainer != null) {
            result = this.parentContainer.getComponentDescriptorMap(role);
        }
        final Map componentDescriptors = this.componentRepository.getComponentDescriptorMap(role);
        if (componentDescriptors != null) {
            if (result != null) {
                result.putAll(componentDescriptors);
            }
            else {
                result = componentDescriptors;
            }
        }
        return result;
    }
    
    public List getComponentDescriptorList(final String role) {
        List result = null;
        final Map componentDescriptorsByHint = this.getComponentDescriptorMap(role);
        if (componentDescriptorsByHint != null) {
            result = new ArrayList(componentDescriptorsByHint.values());
        }
        else {
            final ComponentDescriptor unhintedDescriptor = this.getComponentDescriptor(role);
            if (unhintedDescriptor != null) {
                result = Collections.singletonList(unhintedDescriptor);
            }
            else {
                result = Collections.EMPTY_LIST;
            }
        }
        return result;
    }
    
    public void addComponentDescriptor(final ComponentDescriptor componentDescriptor) throws ComponentRepositoryException {
        this.componentRepository.addComponentDescriptor(componentDescriptor);
    }
    
    public void release(final Object component) throws ComponentLifecycleException {
        if (component == null) {
            return;
        }
        final ComponentManager componentManager = this.componentManagerManager.findComponentManagerByComponentInstance(component);
        if (componentManager == null) {
            if (this.parentContainer != null) {
                this.parentContainer.release(component);
            }
            else {
                this.getLogger().warn("Component manager not found for returned component. Ignored. component=" + component);
            }
        }
        else {
            componentManager.release(component);
            if (componentManager.getConnections() <= 0) {
                this.componentManagerManager.unassociateComponentWithComponentManager(component);
            }
        }
    }
    
    public void releaseAll(final Map components) throws ComponentLifecycleException {
        for (final Object component : components.values()) {
            this.release(component);
        }
    }
    
    public void releaseAll(final List components) throws ComponentLifecycleException {
        for (final Object component : components) {
            this.release(component);
        }
    }
    
    public boolean hasComponent(final String componentKey) {
        return this.componentRepository.hasComponent(componentKey);
    }
    
    public boolean hasComponent(final String role, final String roleHint) {
        return this.componentRepository.hasComponent(role, roleHint);
    }
    
    public void suspend(final Object component) throws ComponentLifecycleException {
        if (component == null) {
            return;
        }
        final ComponentManager componentManager = this.componentManagerManager.findComponentManagerByComponentInstance(component);
        componentManager.suspend(component);
    }
    
    public void resume(final Object component) throws ComponentLifecycleException {
        if (component == null) {
            return;
        }
        final ComponentManager componentManager = this.componentManagerManager.findComponentManagerByComponentInstance(component);
        componentManager.resume(component);
    }
    
    public ClassRealm getComponentRealm(final String id) {
        return this.plexusRealm;
    }
    
    public boolean isInitialized() {
        return this.initialized;
    }
    
    public void initialize() throws PlexusContainerException {
        try {
            this.initializeClassWorlds();
            this.initializeConfiguration();
            this.initializeResources();
            this.initializeCoreComponents();
            this.initializeLoggerManager();
            this.initializeContext();
            this.initializeSystemProperties();
            this.initialized = true;
        }
        catch (DuplicateRealmException e) {
            throw new PlexusContainerException("Error initializing classworlds", e);
        }
        catch (ConfigurationProcessingException e2) {
            throw new PlexusContainerException("Error processing configuration", e2);
        }
        catch (ConfigurationResourceNotFoundException e3) {
            throw new PlexusContainerException("Error processing configuration", e3);
        }
        catch (ComponentConfigurationException e4) {
            throw new PlexusContainerException("Error configuring components", e4);
        }
        catch (PlexusConfigurationException e5) {
            throw new PlexusContainerException("Error configuring components", e5);
        }
        catch (ComponentRepositoryException e6) {
            throw new PlexusContainerException("Error initializing components", e6);
        }
        catch (ContextException e7) {
            throw new PlexusContainerException("Error contextualizing components", e7);
        }
    }
    
    public void registerComponentDiscoveryListeners() throws ComponentLookupException {
        final List listeners = this.componentDiscovererManager.getListenerDescriptors();
        if (listeners != null) {
            for (final DiscoveryListenerDescriptor listenerDescriptor : listeners) {
                final String role = listenerDescriptor.getRole();
                final ComponentDiscoveryListener l = (ComponentDiscoveryListener)this.lookup(role);
                this.componentDiscovererManager.registerComponentDiscoveryListener(l);
            }
        }
    }
    
    public List discoverComponents(final ClassRealm classRealm) throws PlexusConfigurationException, ComponentRepositoryException {
        final List discoveredComponentDescriptors = new ArrayList();
        for (final ComponentDiscoverer componentDiscoverer : this.componentDiscovererManager.getComponentDiscoverers()) {
            final List componentSetDescriptors = componentDiscoverer.findComponents(this.getContext(), classRealm);
            for (final ComponentSetDescriptor componentSet : componentSetDescriptors) {
                final List componentDescriptors = componentSet.getComponents();
                if (componentDescriptors != null) {
                    for (final ComponentDescriptor componentDescriptor : componentDescriptors) {
                        componentDescriptor.setComponentSetDescriptor(componentSet);
                        if (this.getComponentDescriptor(componentDescriptor.getComponentKey()) == null) {
                            this.addComponentDescriptor(componentDescriptor);
                            discoveredComponentDescriptors.add(componentDescriptor);
                        }
                    }
                }
            }
        }
        return discoveredComponentDescriptors;
    }
    
    public boolean isStarted() {
        return this.started;
    }
    
    public void start() throws PlexusContainerException {
        try {
            this.registerComponentDiscoveryListeners();
            this.discoverComponents(this.plexusRealm);
            this.loadComponentsOnStart();
            this.started = true;
        }
        catch (PlexusConfigurationException e) {
            throw new PlexusContainerException("Error starting container", e);
        }
        catch (ComponentLookupException e2) {
            throw new PlexusContainerException("Error starting container", e2);
        }
        catch (ComponentRepositoryException e3) {
            throw new PlexusContainerException("Error starting container", e3);
        }
        this.configuration = null;
    }
    
    public void dispose() {
        this.disposeAllComponents();
        if (this.parentContainer != null) {
            this.parentContainer.removeChildContainer(this.getName());
            this.parentContainer = null;
        }
        try {
            this.plexusRealm.setParent(null);
            this.classWorld.disposeRealm(this.plexusRealm.getId());
        }
        catch (NoSuchRealmException e) {
            this.getLogger().debug("Failed to dispose realm for exiting container: " + this.getName(), e);
        }
        this.started = false;
        this.initialized = true;
    }
    
    protected void disposeAllComponents() {
        final Collection collection = new ArrayList(this.componentManagerManager.getComponentManagers().values());
        final Iterator iter = collection.iterator();
        while (iter.hasNext()) {
            try {
                iter.next().dispose();
            }
            catch (Exception e) {
                this.getLogger().error("Error while disposing component manager. Continuing with the rest", e);
            }
        }
        this.componentManagerManager.getComponentManagers().clear();
    }
    
    public void setParentPlexusContainer(final PlexusContainer parentContainer) {
        this.parentContainer = parentContainer;
    }
    
    public void addContextValue(final Object key, final Object value) {
        this.context.put(key, value);
    }
    
    public void setConfigurationResource(final Reader configuration) throws PlexusConfigurationResourceException {
        this.configurationReader = configuration;
    }
    
    protected void loadComponentsOnStart() throws PlexusConfigurationException, ComponentLookupException {
        final PlexusConfiguration[] loadOnStartComponents = this.configuration.getChild("load-on-start").getChildren("component");
        this.getLogger().debug("Found " + loadOnStartComponents.length + " components to load on start");
        for (int i = 0; i < loadOnStartComponents.length; ++i) {
            final String role = loadOnStartComponents[i].getChild("role").getValue(null);
            final String roleHint = loadOnStartComponents[i].getChild("role-hint").getValue();
            if (role == null) {
                throw new PlexusConfigurationException("Missing 'role' element from load-on-start.");
            }
            if (roleHint == null) {
                this.getLogger().info("Loading on start [role]: [" + role + "]");
                this.lookup(role);
            }
            else if (roleHint.equals("*")) {
                this.getLogger().info("Loading on start all components with [role]: [" + role + "]");
                this.lookupList(role);
            }
            else {
                this.getLogger().info("Loading on start [role,roleHint]: [" + role + "," + roleHint + "]");
                this.lookup(role, roleHint);
            }
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public ClassWorld getClassWorld() {
        return this.classWorld;
    }
    
    public void setClassWorld(final ClassWorld classWorld) {
        this.classWorld = classWorld;
    }
    
    public ClassRealm getCoreRealm() {
        return this.coreRealm;
    }
    
    public void setCoreRealm(final ClassRealm coreRealm) {
        this.coreRealm = coreRealm;
    }
    
    private void initializeClassWorlds() throws DuplicateRealmException {
        if (this.classWorld == null) {
            this.classWorld = new ClassWorld();
        }
        this.initializeName();
        if (this.coreRealm == null) {
            try {
                this.coreRealm = this.classWorld.getRealm("plexus.core");
            }
            catch (NoSuchRealmException e) {
                this.coreRealm = this.classWorld.newRealm("plexus.core", Thread.currentThread().getContextClassLoader());
            }
        }
        if (this.plexusRealm == null) {
            try {
                this.plexusRealm = this.coreRealm.getWorld().getRealm("plexus.core.maven");
            }
            catch (NoSuchRealmException e) {
                this.plexusRealm = this.coreRealm;
            }
            this.addContextValue("common.classloader", this.plexusRealm.getClassLoader());
            Thread.currentThread().setContextClassLoader(this.plexusRealm.getClassLoader());
        }
    }
    
    public ClassRealm getContainerRealm() {
        return this.plexusRealm;
    }
    
    protected void initializeName() {
        if (this.name == null) {
            int i = 0;
            try {
                while (true) {
                    this.classWorld.getRealm("plexus.app" + i);
                    ++i;
                }
            }
            catch (NoSuchRealmException e) {
                this.setName("app" + i);
            }
        }
    }
    
    public Context getContext() {
        return this.context;
    }
    
    private void initializeContext() {
        this.addContextValue("plexus", this);
        this.addContextValue("coreRealm", this.plexusRealm);
    }
    
    protected void initializeConfiguration() throws ConfigurationProcessingException, ConfigurationResourceNotFoundException, PlexusConfigurationException {
        final InputStream is = this.coreRealm.getResourceAsStream("org/codehaus/plexus/plexus-bootstrap.xml");
        if (is == null) {
            throw new IllegalStateException("The internal default plexus-bootstrap.xml is missing. This is highly irregular, your plexus JAR is most likely corrupt.");
        }
        final PlexusConfiguration systemConfiguration = PlexusTools.buildConfiguration("org/codehaus/plexus/plexus-bootstrap.xml", new InputStreamReader(is));
        this.configuration = systemConfiguration;
        final PlexusXmlComponentDiscoverer discoverer = new PlexusXmlComponentDiscoverer();
        final PlexusConfiguration plexusConfiguration = discoverer.discoverConfiguration(this.getContext(), this.plexusRealm);
        if (plexusConfiguration != null) {
            this.configuration = PlexusConfigurationMerger.merge(plexusConfiguration, this.configuration);
            this.processConfigurationsDirectory();
        }
        if (this.configurationReader != null) {
            final PlexusConfiguration userConfiguration = PlexusTools.buildConfiguration("<User Specified Configuration Reader>", this.getInterpolationConfigurationReader(this.configurationReader));
            this.configuration = PlexusConfigurationMerger.merge(userConfiguration, this.configuration);
            this.processConfigurationsDirectory();
        }
        final ConfigurationProcessor p = new ConfigurationProcessor();
        p.addConfigurationResourceHandler(new FileConfigurationResourceHandler());
        p.addConfigurationResourceHandler(new DirectoryConfigurationResourceHandler());
        this.configuration = p.process(this.configuration, Collections.EMPTY_MAP);
    }
    
    protected Reader getInterpolationConfigurationReader(final Reader reader) {
        final InterpolationFilterReader interpolationFilterReader = new InterpolationFilterReader(reader, new ContextMapAdapter(this.context));
        return interpolationFilterReader;
    }
    
    private void processConfigurationsDirectory() throws PlexusConfigurationException {
        final String s = this.configuration.getChild("configurations-directory").getValue(null);
        if (s != null) {
            final PlexusConfiguration componentsConfiguration = this.configuration.getChild("components");
            final File configurationsDirectory = new File(s);
            if (configurationsDirectory.exists() && configurationsDirectory.isDirectory()) {
                List componentConfigurationFiles = null;
                try {
                    componentConfigurationFiles = FileUtils.getFiles(configurationsDirectory, "**/*.conf", "**/*.xml");
                }
                catch (IOException e) {
                    throw new PlexusConfigurationException("Unable to locate configuration files", e);
                }
                for (final File componentConfigurationFile : componentConfigurationFiles) {
                    FileReader reader = null;
                    try {
                        reader = new FileReader(componentConfigurationFile);
                        final PlexusConfiguration componentConfiguration = PlexusTools.buildConfiguration(componentConfigurationFile.getAbsolutePath(), this.getInterpolationConfigurationReader(reader));
                        componentsConfiguration.addChild(componentConfiguration.getChild("components"));
                    }
                    catch (FileNotFoundException e2) {
                        throw new PlexusConfigurationException("File " + componentConfigurationFile + " disappeared before processing", e2);
                    }
                    finally {
                        IOUtil.close(reader);
                    }
                }
            }
        }
    }
    
    private void initializeLoggerManager() throws PlexusContainerException {
        if (this.loggerManager == null) {
            try {
                this.loggerManager = (LoggerManager)this.lookup(LoggerManager.ROLE);
            }
            catch (ComponentLookupException e) {
                throw new PlexusContainerException("Unable to locate logger manager", e);
            }
        }
        this.enableLogging(this.loggerManager.getLoggerForComponent(PlexusContainer.class.getName()));
    }
    
    private void initializeCoreComponents() throws ComponentConfigurationException, ComponentRepositoryException, ContextException {
        final BasicComponentConfigurator configurator = new BasicComponentConfigurator();
        PlexusConfiguration c = this.configuration.getChild("component-repository");
        this.processCoreComponentConfiguration("component-repository", configurator, c);
        this.componentRepository.configure(this.configuration);
        this.componentRepository.setClassRealm(this.plexusRealm);
        this.componentRepository.initialize();
        c = this.configuration.getChild("lifecycle-handler-manager");
        this.processCoreComponentConfiguration("lifecycle-handler-manager", configurator, c);
        this.lifecycleHandlerManager.initialize();
        c = this.configuration.getChild("component-manager-manager");
        this.processCoreComponentConfiguration("component-manager-manager", configurator, c);
        this.componentManagerManager.setLifecycleHandlerManager(this.lifecycleHandlerManager);
        c = this.configuration.getChild("component-discoverer-manager");
        this.processCoreComponentConfiguration("component-discoverer-manager", configurator, c);
        this.componentDiscovererManager.initialize();
        c = this.configuration.getChild("component-factory-manager");
        this.processCoreComponentConfiguration("component-factory-manager", configurator, c);
        if (this.componentFactoryManager instanceof Contextualizable) {
            final Context context = this.getContext();
            context.put("plexus", this);
            ((Contextualizable)this.componentFactoryManager).contextualize(this.getContext());
        }
        c = this.configuration.getChild("component-composer-manager");
        this.processCoreComponentConfiguration("component-composer-manager", configurator, c);
    }
    
    private void processCoreComponentConfiguration(final String role, final BasicComponentConfigurator configurator, final PlexusConfiguration c) throws ComponentConfigurationException {
        final String implementation = c.getAttribute("implementation", null);
        if (implementation == null) {
            final String msg = "Core component: '" + role + "' + which is needed by plexus to function properly cannot " + "be instantiated. Implementation attribute was not specified in plexus.conf." + "This is highly irregular, your plexus JAR is most likely corrupt.";
            throw new ComponentConfigurationException(msg);
        }
        final ComponentDescriptor componentDescriptor = new ComponentDescriptor();
        componentDescriptor.setRole(role);
        componentDescriptor.setImplementation(implementation);
        final PlexusConfiguration configuration = new XmlPlexusConfiguration("configuration");
        configuration.addChild(c);
        try {
            configurator.configureComponent(this, configuration, this.plexusRealm);
        }
        catch (ComponentConfigurationException e) {
            final String message = "Error configuring component: " + componentDescriptor.getHumanReadableKey();
            throw new ComponentConfigurationException(message, e);
        }
    }
    
    private void initializeSystemProperties() throws PlexusConfigurationException {
        final PlexusConfiguration[] systemProperties = this.configuration.getChild("system-properties").getChildren("property");
        for (int i = 0; i < systemProperties.length; ++i) {
            final String name = systemProperties[i].getAttribute("name");
            final String value = systemProperties[i].getAttribute("value");
            if (name == null) {
                throw new PlexusConfigurationException("Missing 'name' attribute in 'property' tag. ");
            }
            if (value == null) {
                throw new PlexusConfigurationException("Missing 'value' attribute in 'property' tag. ");
            }
            System.getProperties().setProperty(name, value);
            this.getLogger().info("Setting system property: [ " + name + ", " + value + " ]");
        }
    }
    
    public void initializeResources() throws PlexusConfigurationException {
        final PlexusConfiguration[] resourceConfigs = this.configuration.getChild("resources").getChildren();
        for (int i = 0; i < resourceConfigs.length; ++i) {
            try {
                final String name = resourceConfigs[i].getName();
                if (name.equals("jar-repository")) {
                    this.addJarRepository(new File(resourceConfigs[i].getValue()));
                }
                else if (name.equals("directory")) {
                    final File directory = new File(resourceConfigs[i].getValue());
                    if (directory.exists() && directory.isDirectory()) {
                        this.plexusRealm.addConstituent(directory.toURL());
                    }
                }
                else {
                    this.getLogger().warn("Unknown resource type: " + name);
                }
            }
            catch (MalformedURLException e) {
                this.getLogger().error("Error configuring resource: " + resourceConfigs[i].getName() + "=" + resourceConfigs[i].getValue(), e);
            }
        }
    }
    
    public void addJarResource(final File jar) throws PlexusContainerException {
        try {
            this.plexusRealm.addConstituent(jar.toURL());
            if (this.isStarted()) {
                this.discoverComponents(this.plexusRealm);
            }
        }
        catch (MalformedURLException e) {
            throw new PlexusContainerException("Cannot add jar resource: " + jar + " (bad URL)", e);
        }
        catch (PlexusConfigurationException e2) {
            throw new PlexusContainerException("Cannot add jar resource: " + jar + " (error discovering new components)", e2);
        }
        catch (ComponentRepositoryException e3) {
            throw new PlexusContainerException("Cannot add jar resource: " + jar + " (error discovering new components)", e3);
        }
    }
    
    public void addJarRepository(final File repository) {
        if (repository.exists() && repository.isDirectory()) {
            final File[] jars = repository.listFiles();
            for (int j = 0; j < jars.length; ++j) {
                if (jars[j].getAbsolutePath().endsWith(".jar")) {
                    try {
                        this.addJarResource(jars[j]);
                    }
                    catch (PlexusContainerException e) {
                        this.getLogger().warn("Unable to add JAR: " + jars[j], e);
                    }
                }
            }
        }
        else {
            this.getLogger().warn("The specified JAR repository doesn't exist or is not a directory: '" + repository.getAbsolutePath() + "'.");
        }
    }
    
    public Logger getLogger() {
        return super.getLogger();
    }
    
    public Object createComponentInstance(final ComponentDescriptor componentDescriptor) throws ComponentInstantiationException, ComponentLifecycleException {
        final String componentFactoryId = componentDescriptor.getComponentFactory();
        ComponentFactory componentFactory = null;
        Object component = null;
        try {
            if (componentFactoryId != null) {
                componentFactory = this.componentFactoryManager.findComponentFactory(componentFactoryId);
            }
            else {
                componentFactory = this.componentFactoryManager.getDefaultComponentFactory();
            }
            component = componentFactory.newInstance(componentDescriptor, this.plexusRealm, this);
        }
        catch (UndefinedComponentFactoryException e) {
            throw new ComponentInstantiationException("Unable to create component as factory '" + componentFactoryId + "' could not be found", e);
        }
        finally {
            if (StringUtils.isNotEmpty(componentFactoryId) && !"java".equals(componentFactoryId)) {
                this.release(componentFactory);
            }
        }
        return component;
    }
    
    public void composeComponent(final Object component, final ComponentDescriptor componentDescriptor) throws CompositionException, UndefinedComponentComposerException {
        this.componentComposerManager.assembleComponent(component, componentDescriptor, this);
    }
    
    public void registerComponentDiscoveryListener(final ComponentDiscoveryListener listener) {
        this.componentDiscovererManager.registerComponentDiscoveryListener(listener);
    }
    
    public void removeComponentDiscoveryListener(final ComponentDiscoveryListener listener) {
        this.componentDiscovererManager.removeComponentDiscoveryListener(listener);
    }
    
    public void setLoggerManager(final LoggerManager loggerManager) {
        this.loggerManager = loggerManager;
    }
    
    public LoggerManager getLoggerManager() {
        return this.loggerManager;
    }
}
