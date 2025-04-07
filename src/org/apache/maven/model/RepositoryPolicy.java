// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.io.Serializable;

public class RepositoryPolicy implements Serializable
{
    private boolean enabled;
    private String updatePolicy;
    private String checksumPolicy;
    
    public RepositoryPolicy() {
        this.enabled = true;
    }
    
    public String getChecksumPolicy() {
        return this.checksumPolicy;
    }
    
    public String getUpdatePolicy() {
        return this.updatePolicy;
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public void setChecksumPolicy(final String checksumPolicy) {
        this.checksumPolicy = checksumPolicy;
    }
    
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
    
    public void setUpdatePolicy(final String updatePolicy) {
        this.updatePolicy = updatePolicy;
    }
}
