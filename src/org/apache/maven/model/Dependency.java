// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Dependency implements Serializable
{
    private String groupId;
    private String artifactId;
    private String version;
    private String type;
    private String classifier;
    private String scope;
    private String systemPath;
    private List<Exclusion> exclusions;
    private boolean optional;
    
    public Dependency() {
        this.type = "jar";
        this.optional = false;
    }
    
    public void addExclusion(final Exclusion exclusion) {
        if (!(exclusion instanceof Exclusion)) {
            throw new ClassCastException("Dependency.addExclusions(exclusion) parameter must be instanceof " + Exclusion.class.getName());
        }
        this.getExclusions().add(exclusion);
    }
    
    public String getArtifactId() {
        return this.artifactId;
    }
    
    public String getClassifier() {
        return this.classifier;
    }
    
    public List<Exclusion> getExclusions() {
        if (this.exclusions == null) {
            this.exclusions = new ArrayList<Exclusion>();
        }
        return this.exclusions;
    }
    
    public String getGroupId() {
        return this.groupId;
    }
    
    public String getScope() {
        return this.scope;
    }
    
    public String getSystemPath() {
        return this.systemPath;
    }
    
    public String getType() {
        return this.type;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public boolean isOptional() {
        return this.optional;
    }
    
    public void removeExclusion(final Exclusion exclusion) {
        if (!(exclusion instanceof Exclusion)) {
            throw new ClassCastException("Dependency.removeExclusions(exclusion) parameter must be instanceof " + Exclusion.class.getName());
        }
        this.getExclusions().remove(exclusion);
    }
    
    public void setArtifactId(final String artifactId) {
        this.artifactId = artifactId;
    }
    
    public void setClassifier(final String classifier) {
        this.classifier = classifier;
    }
    
    public void setExclusions(final List<Exclusion> exclusions) {
        this.exclusions = exclusions;
    }
    
    public void setGroupId(final String groupId) {
        this.groupId = groupId;
    }
    
    public void setOptional(final boolean optional) {
        this.optional = optional;
    }
    
    public void setScope(final String scope) {
        this.scope = scope;
    }
    
    public void setSystemPath(final String systemPath) {
        this.systemPath = systemPath;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public void setVersion(final String version) {
        this.version = version;
    }
    
    @Override
    public String toString() {
        return "Dependency {groupId=" + this.groupId + ", artifactId=" + this.artifactId + ", version=" + this.version + ", type=" + this.type + "}";
    }
    
    public String getManagementKey() {
        return this.groupId + ":" + this.artifactId + ":" + this.type + ((this.classifier != null) ? (":" + this.classifier) : "");
    }
}
