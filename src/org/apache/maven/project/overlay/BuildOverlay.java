// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project.overlay;

import java.util.Map;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.Resource;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Extension;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.model.Build;

public class BuildOverlay extends Build
{
    private final Build build;
    private List resources;
    private List testResources;
    
    public BuildOverlay(final Build build) {
        if (build == null) {
            this.build = new Build();
            this.resources = new ArrayList();
            this.testResources = new ArrayList();
        }
        else {
            this.build = build;
            this.resources = new ArrayList(build.getResources());
            this.testResources = new ArrayList(build.getTestResources());
        }
    }
    
    @Override
    public void addExtension(final Extension extension) {
        this.build.addExtension(extension);
    }
    
    @Override
    public void addPlugin(final Plugin plugin) {
        this.build.addPlugin(plugin);
    }
    
    @Override
    public void addResource(final Resource resource) {
        this.resources.add(resource);
    }
    
    @Override
    public void addTestResource(final Resource resource) {
        this.testResources.add(resource);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this.build.equals(obj);
    }
    
    @Override
    public void flushPluginMap() {
        this.build.flushPluginMap();
    }
    
    @Override
    public String getDefaultGoal() {
        return this.build.getDefaultGoal();
    }
    
    @Override
    public String getDirectory() {
        return this.build.getDirectory();
    }
    
    @Override
    public List getExtensions() {
        return this.build.getExtensions();
    }
    
    @Override
    public String getFinalName() {
        return this.build.getFinalName();
    }
    
    @Override
    public String getOutputDirectory() {
        return this.build.getOutputDirectory();
    }
    
    @Override
    public PluginManagement getPluginManagement() {
        return this.build.getPluginManagement();
    }
    
    @Override
    public List getPlugins() {
        return this.build.getPlugins();
    }
    
    @Override
    public Map getPluginsAsMap() {
        return this.build.getPluginsAsMap();
    }
    
    @Override
    public List getResources() {
        return this.resources;
    }
    
    @Override
    public String getScriptSourceDirectory() {
        return this.build.getScriptSourceDirectory();
    }
    
    @Override
    public String getSourceDirectory() {
        return this.build.getSourceDirectory();
    }
    
    @Override
    public String getTestOutputDirectory() {
        return this.build.getTestOutputDirectory();
    }
    
    @Override
    public List getTestResources() {
        return this.testResources;
    }
    
    @Override
    public String getTestSourceDirectory() {
        return this.build.getTestSourceDirectory();
    }
    
    @Override
    public int hashCode() {
        return this.build.hashCode();
    }
    
    @Override
    public void removeExtension(final Extension extension) {
        this.build.removeExtension(extension);
    }
    
    @Override
    public void removePlugin(final Plugin plugin) {
        this.build.removePlugin(plugin);
    }
    
    @Override
    public void removeResource(final Resource resource) {
        this.resources.remove(resource);
    }
    
    @Override
    public void removeTestResource(final Resource resource) {
        this.testResources.remove(resource);
    }
    
    @Override
    public void setDefaultGoal(final String defaultGoal) {
        this.build.setDefaultGoal(defaultGoal);
    }
    
    @Override
    public void setDirectory(final String directory) {
        this.build.setDirectory(directory);
    }
    
    @Override
    public void setExtensions(final List extensions) {
        this.build.setExtensions(extensions);
    }
    
    @Override
    public void setFinalName(final String finalName) {
        this.build.setFinalName(finalName);
    }
    
    @Override
    public void setOutputDirectory(final String outputDirectory) {
        this.build.setOutputDirectory(outputDirectory);
    }
    
    @Override
    public void setPluginManagement(final PluginManagement pluginManagement) {
        this.build.setPluginManagement(pluginManagement);
    }
    
    @Override
    public void setPlugins(final List plugins) {
        this.build.setPlugins(plugins);
    }
    
    @Override
    public void setResources(final List resources) {
        this.resources = resources;
    }
    
    @Override
    public void setScriptSourceDirectory(final String scriptSourceDirectory) {
        this.build.setScriptSourceDirectory(scriptSourceDirectory);
    }
    
    @Override
    public void setSourceDirectory(final String sourceDirectory) {
        this.build.setSourceDirectory(sourceDirectory);
    }
    
    @Override
    public void setTestOutputDirectory(final String testOutputDirectory) {
        this.build.setTestOutputDirectory(testOutputDirectory);
    }
    
    @Override
    public void setTestResources(final List testResources) {
        this.testResources = testResources;
    }
    
    @Override
    public void setTestSourceDirectory(final String testSourceDirectory) {
        this.build.setTestSourceDirectory(testSourceDirectory);
    }
    
    @Override
    public String toString() {
        return this.build.toString();
    }
    
    @Override
    public void addFilter(final String string) {
        this.build.addFilter(string);
    }
    
    @Override
    public List getFilters() {
        return this.build.getFilters();
    }
    
    @Override
    public void removeFilter(final String string) {
        this.build.removeFilter(string);
    }
    
    @Override
    public void setFilters(final List filters) {
        this.build.setFilters(filters);
    }
}
