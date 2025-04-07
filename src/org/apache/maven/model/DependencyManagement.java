// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class DependencyManagement implements Serializable
{
    private List<Dependency> dependencies;
    
    public void addDependency(final Dependency dependency) {
        if (!(dependency instanceof Dependency)) {
            throw new ClassCastException("DependencyManagement.addDependencies(dependency) parameter must be instanceof " + Dependency.class.getName());
        }
        this.getDependencies().add(dependency);
    }
    
    public List<Dependency> getDependencies() {
        if (this.dependencies == null) {
            this.dependencies = new ArrayList<Dependency>();
        }
        return this.dependencies;
    }
    
    public void removeDependency(final Dependency dependency) {
        if (!(dependency instanceof Dependency)) {
            throw new ClassCastException("DependencyManagement.removeDependencies(dependency) parameter must be instanceof " + Dependency.class.getName());
        }
        this.getDependencies().remove(dependency);
    }
    
    public void setDependencies(final List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }
}
