// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.repository.metadata;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Versioning implements Serializable
{
    private String latest;
    private String release;
    private Snapshot snapshot;
    private List<String> versions;
    private String lastUpdated;
    
    public void addVersion(final String string) {
        if (!(string instanceof String)) {
            throw new ClassCastException("Versioning.addVersions(string) parameter must be instanceof " + String.class.getName());
        }
        this.getVersions().add(string);
    }
    
    public String getLastUpdated() {
        return this.lastUpdated;
    }
    
    public String getLatest() {
        return this.latest;
    }
    
    public String getRelease() {
        return this.release;
    }
    
    public Snapshot getSnapshot() {
        return this.snapshot;
    }
    
    public List<String> getVersions() {
        if (this.versions == null) {
            this.versions = new ArrayList<String>();
        }
        return this.versions;
    }
    
    public void removeVersion(final String string) {
        if (!(string instanceof String)) {
            throw new ClassCastException("Versioning.removeVersions(string) parameter must be instanceof " + String.class.getName());
        }
        this.getVersions().remove(string);
    }
    
    public void setLastUpdated(final String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public void setLatest(final String latest) {
        this.latest = latest;
    }
    
    public void setRelease(final String release) {
        this.release = release;
    }
    
    public void setSnapshot(final Snapshot snapshot) {
        this.snapshot = snapshot;
    }
    
    public void setVersions(final List<String> versions) {
        this.versions = versions;
    }
    
    public void updateTimestamp() {
        this.setLastUpdatedTimestamp(new Date());
    }
    
    public void setLastUpdatedTimestamp(final Date date) {
        final TimeZone timezone = TimeZone.getTimeZone("UTC");
        final DateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
        fmt.setTimeZone(timezone);
        this.setLastUpdated(fmt.format(date));
    }
}
