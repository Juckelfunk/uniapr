// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class BuildBase extends PluginConfiguration implements Serializable
{
    private String defaultGoal;
    private List<Resource> resources;
    private List<Resource> testResources;
    private String directory;
    private String finalName;
    private List<String> filters;
    
    public void addFilter(final String string) {
        if (!(string instanceof String)) {
            throw new ClassCastException("BuildBase.addFilters(string) parameter must be instanceof " + String.class.getName());
        }
        this.getFilters().add(string);
    }
    
    public void addResource(final Resource resource) {
        if (!(resource instanceof Resource)) {
            throw new ClassCastException("BuildBase.addResources(resource) parameter must be instanceof " + Resource.class.getName());
        }
        this.getResources().add(resource);
    }
    
    public void addTestResource(final Resource resource) {
        if (!(resource instanceof Resource)) {
            throw new ClassCastException("BuildBase.addTestResources(resource) parameter must be instanceof " + Resource.class.getName());
        }
        this.getTestResources().add(resource);
    }
    
    public String getDefaultGoal() {
        return this.defaultGoal;
    }
    
    public String getDirectory() {
        return this.directory;
    }
    
    public List<String> getFilters() {
        if (this.filters == null) {
            this.filters = new ArrayList<String>();
        }
        return this.filters;
    }
    
    public String getFinalName() {
        return this.finalName;
    }
    
    public List<Resource> getResources() {
        if (this.resources == null) {
            this.resources = new ArrayList<Resource>();
        }
        return this.resources;
    }
    
    public List<Resource> getTestResources() {
        if (this.testResources == null) {
            this.testResources = new ArrayList<Resource>();
        }
        return this.testResources;
    }
    
    public void removeFilter(final String string) {
        if (!(string instanceof String)) {
            throw new ClassCastException("BuildBase.removeFilters(string) parameter must be instanceof " + String.class.getName());
        }
        this.getFilters().remove(string);
    }
    
    public void removeResource(final Resource resource) {
        if (!(resource instanceof Resource)) {
            throw new ClassCastException("BuildBase.removeResources(resource) parameter must be instanceof " + Resource.class.getName());
        }
        this.getResources().remove(resource);
    }
    
    public void removeTestResource(final Resource resource) {
        if (!(resource instanceof Resource)) {
            throw new ClassCastException("BuildBase.removeTestResources(resource) parameter must be instanceof " + Resource.class.getName());
        }
        this.getTestResources().remove(resource);
    }
    
    public void setDefaultGoal(final String defaultGoal) {
        this.defaultGoal = defaultGoal;
    }
    
    public void setDirectory(final String directory) {
        this.directory = directory;
    }
    
    public void setFilters(final List<String> filters) {
        this.filters = filters;
    }
    
    public void setFinalName(final String finalName) {
        this.finalName = finalName;
    }
    
    public void setResources(final List<Resource> resources) {
        this.resources = resources;
    }
    
    public void setTestResources(final List<Resource> testResources) {
        this.testResources = testResources;
    }
}
