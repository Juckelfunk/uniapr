// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.io.Serializable;

public class Parent implements Serializable
{
    private String artifactId;
    private String groupId;
    private String version;
    private String relativePath;
    
    public Parent() {
        this.relativePath = "../pom.xml";
    }
    
    public String getArtifactId() {
        return this.artifactId;
    }
    
    public String getGroupId() {
        return this.groupId;
    }
    
    public String getRelativePath() {
        return this.relativePath;
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
    
    public void setRelativePath(final String relativePath) {
        this.relativePath = relativePath;
    }
    
    public void setVersion(final String version) {
        this.version = version;
    }
    
    public String getId() {
        final StringBuffer id = new StringBuffer();
        id.append(this.getGroupId());
        id.append(":");
        id.append(this.getArtifactId());
        id.append(":");
        id.append("pom");
        id.append(":");
        id.append(this.getVersion());
        return id.toString();
    }
}
