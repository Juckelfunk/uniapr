// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.profiles;

import java.io.Serializable;

public class ActivationFile implements Serializable
{
    private String missing;
    private String exists;
    
    public String getExists() {
        return this.exists;
    }
    
    public String getMissing() {
        return this.missing;
    }
    
    public void setExists(final String exists) {
        this.exists = exists;
    }
    
    public void setMissing(final String missing) {
        this.missing = missing;
    }
}
