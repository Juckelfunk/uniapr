// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.io.Serializable;

public class ConfigurationContainer implements Serializable
{
    private String inherited;
    private Object configuration;
    private boolean inheritanceApplied;
    
    public ConfigurationContainer() {
        this.inheritanceApplied = true;
    }
    
    public Object getConfiguration() {
        return this.configuration;
    }
    
    public String getInherited() {
        return this.inherited;
    }
    
    public void setConfiguration(final Object configuration) {
        this.configuration = configuration;
    }
    
    public void setInherited(final String inherited) {
        this.inherited = inherited;
    }
    
    public void unsetInheritanceApplied() {
        this.inheritanceApplied = false;
    }
    
    public boolean isInheritanceApplied() {
        return this.inheritanceApplied;
    }
}
