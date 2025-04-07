// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.repository;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.plexus.configuration.PlexusConfiguration;

public class ComponentDescriptor
{
    private String alias;
    private String role;
    private String roleHint;
    private String implementation;
    private String version;
    private String componentType;
    private PlexusConfiguration configuration;
    private String instantiationStrategy;
    private String lifecycleHandler;
    private String componentProfile;
    private List requirements;
    private String componentFactory;
    private String componentComposer;
    private String componentConfigurator;
    private String description;
    private boolean isolatedRealm;
    private List dependencies;
    private ComponentSetDescriptor componentSetDescriptor;
    
    public ComponentDescriptor() {
        this.alias = null;
        this.role = null;
        this.roleHint = null;
        this.implementation = null;
        this.version = null;
        this.componentType = null;
        this.configuration = null;
        this.instantiationStrategy = null;
        this.lifecycleHandler = null;
        this.componentProfile = null;
    }
    
    public String getComponentKey() {
        if (this.getRoleHint() != null) {
            return this.getRole() + this.getRoleHint();
        }
        return this.getRole();
    }
    
    public String getHumanReadableKey() {
        final StringBuffer key = new StringBuffer();
        key.append("role: '" + this.role + "'");
        key.append(", implementation: '" + this.implementation + "'");
        if (this.roleHint != null) {
            key.append(", role hint: '" + this.roleHint + "'");
        }
        if (this.alias != null) {
            key.append(", alias: '" + this.alias + "'");
        }
        return key.toString();
    }
    
    public String getAlias() {
        return this.alias;
    }
    
    public void setAlias(final String alias) {
        this.alias = alias;
    }
    
    public String getRole() {
        return this.role;
    }
    
    public void setRole(final String role) {
        this.role = role;
    }
    
    public String getRoleHint() {
        return this.roleHint;
    }
    
    public void setRoleHint(final String roleHint) {
        this.roleHint = roleHint;
    }
    
    public String getImplementation() {
        return this.implementation;
    }
    
    public void setImplementation(final String implementation) {
        this.implementation = implementation;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public void setVersion(final String version) {
        this.version = version;
    }
    
    public String getComponentType() {
        return this.componentType;
    }
    
    public void setComponentType(final String componentType) {
        this.componentType = componentType;
    }
    
    public String getInstantiationStrategy() {
        return this.instantiationStrategy;
    }
    
    public PlexusConfiguration getConfiguration() {
        return this.configuration;
    }
    
    public void setConfiguration(final PlexusConfiguration configuration) {
        this.configuration = configuration;
    }
    
    public boolean hasConfiguration() {
        return this.configuration != null;
    }
    
    public String getLifecycleHandler() {
        return this.lifecycleHandler;
    }
    
    public void setLifecycleHandler(final String lifecycleHandler) {
        this.lifecycleHandler = lifecycleHandler;
    }
    
    public String getComponentProfile() {
        return this.componentProfile;
    }
    
    public void setComponentProfile(final String componentProfile) {
        this.componentProfile = componentProfile;
    }
    
    public void addRequirement(final ComponentRequirement requirement) {
        this.getRequirements().add(requirement);
    }
    
    public List getRequirements() {
        if (this.requirements == null) {
            this.requirements = new ArrayList();
        }
        return this.requirements;
    }
    
    public String getComponentFactory() {
        return this.componentFactory;
    }
    
    public void setComponentFactory(final String componentFactory) {
        this.componentFactory = componentFactory;
    }
    
    public String getComponentComposer() {
        return this.componentComposer;
    }
    
    public void setComponentComposer(final String componentComposer) {
        this.componentComposer = componentComposer;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public void setInstantiationStrategy(final String instantiationStrategy) {
        this.instantiationStrategy = instantiationStrategy;
    }
    
    public boolean isIsolatedRealm() {
        return this.isolatedRealm;
    }
    
    public void setComponentSetDescriptor(final ComponentSetDescriptor componentSetDescriptor) {
        this.componentSetDescriptor = componentSetDescriptor;
    }
    
    public ComponentSetDescriptor getComponentSetDescriptor() {
        return this.componentSetDescriptor;
    }
    
    public void setIsolatedRealm(final boolean isolatedRealm) {
        this.isolatedRealm = isolatedRealm;
    }
    
    public List getDependencies() {
        return this.dependencies;
    }
    
    public String getComponentConfigurator() {
        return this.componentConfigurator;
    }
    
    public void setComponentConfigurator(final String componentConfigurator) {
        this.componentConfigurator = componentConfigurator;
    }
    
    public boolean equals(final Object other) {
        if (!(other instanceof ComponentDescriptor)) {
            return false;
        }
        final ComponentDescriptor otherDescriptor = (ComponentDescriptor)other;
        boolean isEqual = true;
        final String role = this.getRole();
        final String otherRole = otherDescriptor.getRole();
        isEqual = (isEqual && (role == otherRole || role.equals(otherRole)));
        final String roleHint = this.getRoleHint();
        final String otherRoleHint = otherDescriptor.getRoleHint();
        isEqual = (isEqual && (roleHint == otherRoleHint || roleHint.equals(otherRoleHint)));
        return isEqual;
    }
    
    public String toString() {
        return this.getClass().getName() + " [role: '" + this.getRole() + "', hint: '" + this.getRoleHint() + "']";
    }
    
    public int hashCode() {
        int result = this.getRole().hashCode() + 1;
        final String hint = this.getRoleHint();
        if (hint != null) {
            result += hint.hashCode();
        }
        return result;
    }
}
