// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.io.Serializable;

public class Plugin extends ConfigurationContainer implements Serializable
{
    private String groupId;
    private String artifactId;
    private String version;
    private boolean extensions;
    private List<PluginExecution> executions;
    private List<Dependency> dependencies;
    private Object goals;
    private Map executionMap;
    private String key;
    
    public Plugin() {
        this.groupId = "org.apache.maven.plugins";
        this.extensions = false;
        this.executionMap = null;
    }
    
    public void addDependency(final Dependency dependency) {
        if (!(dependency instanceof Dependency)) {
            throw new ClassCastException("Plugin.addDependencies(dependency) parameter must be instanceof " + Dependency.class.getName());
        }
        this.getDependencies().add(dependency);
    }
    
    public void addExecution(final PluginExecution pluginExecution) {
        if (!(pluginExecution instanceof PluginExecution)) {
            throw new ClassCastException("Plugin.addExecutions(pluginExecution) parameter must be instanceof " + PluginExecution.class.getName());
        }
        this.getExecutions().add(pluginExecution);
    }
    
    public String getArtifactId() {
        return this.artifactId;
    }
    
    public List<Dependency> getDependencies() {
        if (this.dependencies == null) {
            this.dependencies = new ArrayList<Dependency>();
        }
        return this.dependencies;
    }
    
    public List<PluginExecution> getExecutions() {
        if (this.executions == null) {
            this.executions = new ArrayList<PluginExecution>();
        }
        return this.executions;
    }
    
    public Object getGoals() {
        return this.goals;
    }
    
    public String getGroupId() {
        return this.groupId;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public boolean isExtensions() {
        return this.extensions;
    }
    
    public void removeDependency(final Dependency dependency) {
        if (!(dependency instanceof Dependency)) {
            throw new ClassCastException("Plugin.removeDependencies(dependency) parameter must be instanceof " + Dependency.class.getName());
        }
        this.getDependencies().remove(dependency);
    }
    
    public void removeExecution(final PluginExecution pluginExecution) {
        if (!(pluginExecution instanceof PluginExecution)) {
            throw new ClassCastException("Plugin.removeExecutions(pluginExecution) parameter must be instanceof " + PluginExecution.class.getName());
        }
        this.getExecutions().remove(pluginExecution);
    }
    
    public void setArtifactId(final String artifactId) {
        this.artifactId = artifactId;
    }
    
    public void setDependencies(final List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }
    
    public void setExecutions(final List<PluginExecution> executions) {
        this.executions = executions;
    }
    
    public void setExtensions(final boolean extensions) {
        this.extensions = extensions;
    }
    
    public void setGoals(final Object goals) {
        this.goals = goals;
    }
    
    public void setGroupId(final String groupId) {
        this.groupId = groupId;
    }
    
    public void setVersion(final String version) {
        this.version = version;
    }
    
    public void flushExecutionMap() {
        this.executionMap = null;
    }
    
    public Map getExecutionsAsMap() {
        if (this.executionMap == null) {
            this.executionMap = new LinkedHashMap();
            if (this.getExecutions() != null) {
                for (final PluginExecution exec : this.getExecutions()) {
                    if (this.executionMap.containsKey(exec.getId())) {
                        throw new IllegalStateException("You cannot have two plugin executions with the same (or missing) <id/> elements.\nOffending execution\n\nId: '" + exec.getId() + "'\nPlugin:'" + this.getKey() + "'\n\n");
                    }
                    this.executionMap.put(exec.getId(), exec);
                }
            }
        }
        return this.executionMap;
    }
    
    public String getKey() {
        if (this.key == null) {
            this.key = constructKey(this.groupId, this.artifactId).intern();
        }
        return this.key;
    }
    
    public static String constructKey(final String groupId, final String artifactId) {
        return groupId + ":" + artifactId;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Plugin) {
            final Plugin otherPlugin = (Plugin)other;
            return this.getKey().equals(otherPlugin.getKey());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.getKey().hashCode();
    }
    
    @Override
    public String toString() {
        return "Plugin [" + this.getKey() + "]";
    }
}
