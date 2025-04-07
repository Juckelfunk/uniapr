// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.util.ArrayList;
import java.util.Properties;
import java.util.List;
import java.io.Serializable;

public class ModelBase implements Serializable
{
    private DistributionManagement distributionManagement;
    private List<String> modules;
    private List<Repository> repositories;
    private List<Repository> pluginRepositories;
    private List<Dependency> dependencies;
    private Object reports;
    private Reporting reporting;
    private DependencyManagement dependencyManagement;
    private Properties properties;
    
    public void addDependency(final Dependency dependency) {
        if (!(dependency instanceof Dependency)) {
            throw new ClassCastException("ModelBase.addDependencies(dependency) parameter must be instanceof " + Dependency.class.getName());
        }
        this.getDependencies().add(dependency);
    }
    
    public void addModule(final String string) {
        if (!(string instanceof String)) {
            throw new ClassCastException("ModelBase.addModules(string) parameter must be instanceof " + String.class.getName());
        }
        this.getModules().add(string);
    }
    
    public void addPluginRepository(final Repository repository) {
        if (!(repository instanceof Repository)) {
            throw new ClassCastException("ModelBase.addPluginRepositories(repository) parameter must be instanceof " + Repository.class.getName());
        }
        this.getPluginRepositories().add(repository);
    }
    
    public void addProperty(final String key, final String value) {
        this.getProperties().put(key, value);
    }
    
    public void addRepository(final Repository repository) {
        if (!(repository instanceof Repository)) {
            throw new ClassCastException("ModelBase.addRepositories(repository) parameter must be instanceof " + Repository.class.getName());
        }
        this.getRepositories().add(repository);
    }
    
    public List<Dependency> getDependencies() {
        if (this.dependencies == null) {
            this.dependencies = new ArrayList<Dependency>();
        }
        return this.dependencies;
    }
    
    public DependencyManagement getDependencyManagement() {
        return this.dependencyManagement;
    }
    
    public DistributionManagement getDistributionManagement() {
        return this.distributionManagement;
    }
    
    public List<String> getModules() {
        if (this.modules == null) {
            this.modules = new ArrayList<String>();
        }
        return this.modules;
    }
    
    public List<Repository> getPluginRepositories() {
        if (this.pluginRepositories == null) {
            this.pluginRepositories = new ArrayList<Repository>();
        }
        return this.pluginRepositories;
    }
    
    public Properties getProperties() {
        if (this.properties == null) {
            this.properties = new Properties();
        }
        return this.properties;
    }
    
    public Reporting getReporting() {
        return this.reporting;
    }
    
    public Object getReports() {
        return this.reports;
    }
    
    public List<Repository> getRepositories() {
        if (this.repositories == null) {
            this.repositories = new ArrayList<Repository>();
        }
        return this.repositories;
    }
    
    public void removeDependency(final Dependency dependency) {
        if (!(dependency instanceof Dependency)) {
            throw new ClassCastException("ModelBase.removeDependencies(dependency) parameter must be instanceof " + Dependency.class.getName());
        }
        this.getDependencies().remove(dependency);
    }
    
    public void removeModule(final String string) {
        if (!(string instanceof String)) {
            throw new ClassCastException("ModelBase.removeModules(string) parameter must be instanceof " + String.class.getName());
        }
        this.getModules().remove(string);
    }
    
    public void removePluginRepository(final Repository repository) {
        if (!(repository instanceof Repository)) {
            throw new ClassCastException("ModelBase.removePluginRepositories(repository) parameter must be instanceof " + Repository.class.getName());
        }
        this.getPluginRepositories().remove(repository);
    }
    
    public void removeRepository(final Repository repository) {
        if (!(repository instanceof Repository)) {
            throw new ClassCastException("ModelBase.removeRepositories(repository) parameter must be instanceof " + Repository.class.getName());
        }
        this.getRepositories().remove(repository);
    }
    
    public void setDependencies(final List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }
    
    public void setDependencyManagement(final DependencyManagement dependencyManagement) {
        this.dependencyManagement = dependencyManagement;
    }
    
    public void setDistributionManagement(final DistributionManagement distributionManagement) {
        this.distributionManagement = distributionManagement;
    }
    
    public void setModules(final List<String> modules) {
        this.modules = modules;
    }
    
    public void setPluginRepositories(final List<Repository> pluginRepositories) {
        this.pluginRepositories = pluginRepositories;
    }
    
    public void setProperties(final Properties properties) {
        this.properties = properties;
    }
    
    public void setReporting(final Reporting reporting) {
        this.reporting = reporting;
    }
    
    public void setReports(final Object reports) {
        this.reports = reports;
    }
    
    public void setRepositories(final List<Repository> repositories) {
        this.repositories = repositories;
    }
}
