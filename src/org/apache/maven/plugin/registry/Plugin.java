// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.plugin.registry;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Plugin extends TrackableBase implements Serializable
{
    private String groupId;
    private String artifactId;
    private String lastChecked;
    private String useVersion;
    private List<String> rejectedVersions;
    public static final String LAST_CHECKED_DATE_FORMAT = "yyyy-MM-dd.HH:mm:ss Z";
    
    public void addRejectedVersion(final String string) {
        if (!(string instanceof String)) {
            throw new ClassCastException("Plugin.addRejectedVersions(string) parameter must be instanceof " + String.class.getName());
        }
        this.getRejectedVersions().add(string);
    }
    
    public String getArtifactId() {
        return this.artifactId;
    }
    
    public String getGroupId() {
        return this.groupId;
    }
    
    public String getLastChecked() {
        return this.lastChecked;
    }
    
    public List<String> getRejectedVersions() {
        if (this.rejectedVersions == null) {
            this.rejectedVersions = new ArrayList<String>();
        }
        return this.rejectedVersions;
    }
    
    public String getUseVersion() {
        return this.useVersion;
    }
    
    public void removeRejectedVersion(final String string) {
        if (!(string instanceof String)) {
            throw new ClassCastException("Plugin.removeRejectedVersions(string) parameter must be instanceof " + String.class.getName());
        }
        this.getRejectedVersions().remove(string);
    }
    
    public void setArtifactId(final String artifactId) {
        this.artifactId = artifactId;
    }
    
    public void setGroupId(final String groupId) {
        this.groupId = groupId;
    }
    
    public void setLastChecked(final String lastChecked) {
        this.lastChecked = lastChecked;
    }
    
    public void setRejectedVersions(final List<String> rejectedVersions) {
        this.rejectedVersions = rejectedVersions;
    }
    
    public void setUseVersion(final String useVersion) {
        this.useVersion = useVersion;
    }
    
    public String getKey() {
        return this.getGroupId() + ":" + this.getArtifactId();
    }
}
