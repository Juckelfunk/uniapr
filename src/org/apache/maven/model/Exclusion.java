// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.io.Serializable;

public class Exclusion implements Serializable
{
    private String artifactId;
    private String groupId;
    
    public String getArtifactId() {
        return this.artifactId;
    }
    
    public String getGroupId() {
        return this.groupId;
    }
    
    public void setArtifactId(final String artifactId) {
        this.artifactId = artifactId;
    }
    
    public void setGroupId(final String groupId) {
        this.groupId = groupId;
    }
}
