// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.io.Serializable;

public class Relocation implements Serializable
{
    private String groupId;
    private String artifactId;
    private String version;
    private String message;
    
    public String getArtifactId() {
        return this.artifactId;
    }
    
    public String getGroupId() {
        return this.groupId;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public void setArtifactId(final String artifactId) {
        this.artifactId = artifactId;
    }
    
    public void setGroupId(final String groupId) {
        this.groupId = groupId;
    }
    
    public void setMessage(final String message) {
        this.message = message;
    }
    
    public void setVersion(final String version) {
        this.version = version;
    }
}
