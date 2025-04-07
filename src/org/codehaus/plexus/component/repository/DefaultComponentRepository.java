// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.repository;

import java.util.List;
import org.codehaus.plexus.component.composition.CompositionException;
import org.codehaus.plexus.component.repository.exception.ComponentImplementationNotFoundException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.component.repository.io.PlexusTools;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import java.util.HashMap;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.component.composition.CompositionResolver;
import java.util.Map;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.logging.AbstractLogEnabled;

public class DefaultComponentRepository extends AbstractLogEnabled implements ComponentRepository
{
    private static String COMPONENTS;
    private static String COMPONENT;
    private PlexusConfiguration configuration;
    private Map componentDescriptorMaps;
    private Map componentDescriptors;
    private CompositionResolver compositionResolver;
    private ClassRealm classRealm;
    
    public DefaultComponentRepository() {
        this.componentDescriptors = new HashMap();
        this.componentDescriptorMaps = new HashMap();
    }
    
    protected PlexusConfiguration getConfiguration() {
        return this.configuration;
    }
    
    public boolean hasComponent(final String role) {
        return this.componentDescriptors.containsKey(role);
    }
    
    public boolean hasComponent(final String role, final String roleHint) {
        return this.componentDescriptors.containsKey(role + roleHint);
    }
    
    public Map getComponentDescriptorMap(final String role) {
        return this.componentDescriptorMaps.get(role);
    }
    
    public ComponentDescriptor getComponentDescriptor(final String key) {
        return this.componentDescriptors.get(key);
    }
    
    public void setClassRealm(final ClassRealm classRealm) {
        this.classRealm = classRealm;
    }
    
    public void configure(final PlexusConfiguration configuration) {
        this.configuration = configuration;
    }
    
    public void initialize() throws ComponentRepositoryException {
        this.initializeComponentDescriptors();
    }
    
    public void initializeComponentDescriptors() throws ComponentRepositoryException {
        this.initializeComponentDescriptorsFromUserConfiguration();
    }
    
    private void initializeComponentDescriptorsFromUserConfiguration() throws ComponentRepositoryException {
        final PlexusConfiguration[] componentConfigurations = this.configuration.getChild(DefaultComponentRepository.COMPONENTS).getChildren(DefaultComponentRepository.COMPONENT);
        for (int i = 0; i < componentConfigurations.length; ++i) {
            this.addComponentDescriptor(componentConfigurations[i]);
        }
    }
    
    public void addComponentDescriptor(final PlexusConfiguration configuration) throws ComponentRepositoryException {
        ComponentDescriptor componentDescriptor = null;
        try {
            componentDescriptor = PlexusTools.buildComponentDescriptor(configuration);
        }
        catch (PlexusConfigurationException e) {
            throw new ComponentRepositoryException("Cannot unmarshall component descriptor:", e);
        }
        this.addComponentDescriptor(componentDescriptor);
    }
    
    public void addComponentDescriptor(final ComponentDescriptor componentDescriptor) throws ComponentRepositoryException {
        try {
            this.validateComponentDescriptor(componentDescriptor);
        }
        catch (ComponentImplementationNotFoundException e) {
            throw new ComponentRepositoryException("Component descriptor validation failed: ", e);
        }
        final String role = componentDescriptor.getRole();
        final String roleHint = componentDescriptor.getRoleHint();
        if (roleHint != null) {
            if (this.componentDescriptors.containsKey(role)) {
                final ComponentDescriptor desc = this.componentDescriptors.get(role);
                if (desc.getRoleHint() == null) {
                    final String message = "Component descriptor " + componentDescriptor.getHumanReadableKey() + " has a hint, but there are other implementations that don't";
                    throw new ComponentRepositoryException(message);
                }
            }
            Map map = this.componentDescriptorMaps.get(role);
            if (map == null) {
                map = new HashMap();
                this.componentDescriptorMaps.put(role, map);
            }
            map.put(roleHint, componentDescriptor);
        }
        else {
            if (this.componentDescriptorMaps.containsKey(role)) {
                final String message2 = "Component descriptor " + componentDescriptor.getHumanReadableKey() + " has no hint, but there are other implementations that do";
                throw new ComponentRepositoryException(message2);
            }
            if (this.componentDescriptors.containsKey(role) && !this.componentDescriptors.get(role).equals(componentDescriptor)) {
                final String message2 = "Component role " + role + " is already in the repository and different to attempted addition of " + componentDescriptor.getHumanReadableKey();
                throw new ComponentRepositoryException(message2);
            }
        }
        try {
            this.compositionResolver.addComponentDescriptor(componentDescriptor);
        }
        catch (CompositionException e2) {
            throw new ComponentRepositoryException(e2.getMessage(), e2);
        }
        this.componentDescriptors.put(componentDescriptor.getComponentKey(), componentDescriptor);
        if (!this.componentDescriptors.containsKey(role)) {
            this.componentDescriptors.put(role, componentDescriptor);
        }
    }
    
    public void validateComponentDescriptor(final ComponentDescriptor componentDescriptor) throws ComponentImplementationNotFoundException {
    }
    
    public List getComponentDependencies(final ComponentDescriptor componentDescriptor) {
        return this.compositionResolver.getRequirements(componentDescriptor.getComponentKey());
    }
    
    static {
        DefaultComponentRepository.COMPONENTS = "components";
        DefaultComponentRepository.COMPONENT = "component";
    }
}
