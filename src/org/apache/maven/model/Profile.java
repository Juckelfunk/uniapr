// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.io.Serializable;

public class Profile extends ModelBase implements Serializable
{
    private String id;
    private Activation activation;
    private BuildBase build;
    private String source;
    
    public Profile() {
        this.source = "pom";
    }
    
    public Activation getActivation() {
        return this.activation;
    }
    
    public BuildBase getBuild() {
        return this.build;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setActivation(final Activation activation) {
        this.activation = activation;
    }
    
    public void setBuild(final BuildBase build) {
        this.build = build;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    public void setSource(final String source) {
        this.source = source;
    }
    
    public String getSource() {
        return this.source;
    }
    
    @Override
    public String toString() {
        return "Profile {id: " + this.getId() + ", source: " + this.getSource() + "}";
    }
}
