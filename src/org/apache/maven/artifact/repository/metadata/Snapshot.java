// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.repository.metadata;

import java.io.Serializable;

public class Snapshot implements Serializable
{
    private String timestamp;
    private int buildNumber;
    private boolean localCopy;
    
    public Snapshot() {
        this.buildNumber = 0;
        this.localCopy = false;
    }
    
    public int getBuildNumber() {
        return this.buildNumber;
    }
    
    public String getTimestamp() {
        return this.timestamp;
    }
    
    public boolean isLocalCopy() {
        return this.localCopy;
    }
    
    public void setBuildNumber(final int buildNumber) {
        this.buildNumber = buildNumber;
    }
    
    public void setLocalCopy(final boolean localCopy) {
        this.localCopy = localCopy;
    }
    
    public void setTimestamp(final String timestamp) {
        this.timestamp = timestamp;
    }
}
