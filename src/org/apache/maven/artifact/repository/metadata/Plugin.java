// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.repository.metadata;

import java.io.Serializable;

public class Plugin implements Serializable
{
    private String name;
    private String prefix;
    private String artifactId;
    
    public String getArtifactId() {
        return this.artifactId;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getPrefix() {
        return this.prefix;
    }
    
    public void setArtifactId(final String artifactId) {
        this.artifactId = artifactId;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }
}
