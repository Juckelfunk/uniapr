// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.profiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.io.Serializable;

public class Profile implements Serializable
{
    private String id;
    private Activation activation;
    private Properties properties;
    private List<Repository> repositories;
    private List<Repository> pluginRepositories;
    
    public void addPluginRepository(final Repository repository) {
        if (!(repository instanceof Repository)) {
            throw new ClassCastException("Profile.addPluginRepositories(repository) parameter must be instanceof " + Repository.class.getName());
        }
        this.getPluginRepositories().add(repository);
    }
    
    public void addProperty(final String key, final String value) {
        this.getProperties().put(key, value);
    }
    
    public void addRepository(final Repository repository) {
        if (!(repository instanceof Repository)) {
            throw new ClassCastException("Profile.addRepositories(repository) parameter must be instanceof " + Repository.class.getName());
        }
        this.getRepositories().add(repository);
    }
    
    public Activation getActivation() {
        return this.activation;
    }
    
    public String getId() {
        return this.id;
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
    
    public List<Repository> getRepositories() {
        if (this.repositories == null) {
            this.repositories = new ArrayList<Repository>();
        }
        return this.repositories;
    }
    
    public void removePluginRepository(final Repository repository) {
        if (!(repository instanceof Repository)) {
            throw new ClassCastException("Profile.removePluginRepositories(repository) parameter must be instanceof " + Repository.class.getName());
        }
        this.getPluginRepositories().remove(repository);
    }
    
    public void removeRepository(final Repository repository) {
        if (!(repository instanceof Repository)) {
            throw new ClassCastException("Profile.removeRepositories(repository) parameter must be instanceof " + Repository.class.getName());
        }
        this.getRepositories().remove(repository);
    }
    
    public void setActivation(final Activation activation) {
        this.activation = activation;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    public void setPluginRepositories(final List<Repository> pluginRepositories) {
        this.pluginRepositories = pluginRepositories;
    }
    
    public void setProperties(final Properties properties) {
        this.properties = properties;
    }
    
    public void setRepositories(final List<Repository> repositories) {
        this.repositories = repositories;
    }
}
