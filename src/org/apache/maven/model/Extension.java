// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.io.Serializable;

public class Extension implements Serializable
{
    private String groupId;
    private String artifactId;
    private String version;
    
    public String getArtifactId() {
        return this.artifactId;
    }
    
    public String getGroupId() {
        return this.groupId;
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
    
    public void setVersion(final String version) {
        this.version = version;
    }
    
    public String getKey() {
        return new StringBuffer(128).append(this.getGroupId()).append(':').append(this.getArtifactId()).toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Extension)) {
            return false;
        }
        final Extension e = (Extension)o;
        return equal(e.getArtifactId(), this.getArtifactId()) && equal(e.getGroupId(), this.getGroupId()) && equal(e.getVersion(), this.getVersion());
    }
    
    private static <T> boolean equal(final T obj1, final T obj2) {
        return (obj1 != null) ? obj1.equals(obj2) : (obj2 == null);
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + ((this.getArtifactId() != null) ? this.getArtifactId().hashCode() : 0);
        result = 37 * result + ((this.getGroupId() != null) ? this.getGroupId().hashCode() : 0);
        result = 37 * result + ((this.getVersion() != null) ? this.getVersion().hashCode() : 0);
        return result;
    }
}
