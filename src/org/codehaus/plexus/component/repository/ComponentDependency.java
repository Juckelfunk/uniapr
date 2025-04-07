// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.repository;

public class ComponentDependency
{
    private static final String DEAULT_DEPENDENCY_TYPE = "jar";
    private String groupId;
    private String artifactId;
    private String type;
    private String version;
    
    public ComponentDependency() {
        this.type = "jar";
    }
    
    public String getArtifactId() {
        return this.artifactId;
    }
    
    public void setArtifactId(final String artifactId) {
        this.artifactId = artifactId;
    }
    
    public String getGroupId() {
        return this.groupId;
    }
    
    public void setGroupId(final String groupId) {
        this.groupId = groupId;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public void setVersion(final String version) {
        this.version = version;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("groupId:artifactId:version:type = " + this.groupId + ":" + this.artifactId + ":" + this.version + ":" + this.type);
        return sb.toString();
    }
}
