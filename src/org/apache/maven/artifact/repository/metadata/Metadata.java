// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.repository.metadata;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Metadata implements Serializable
{
    private String groupId;
    private String artifactId;
    private String version;
    private Versioning versioning;
    private List<Plugin> plugins;
    private String modelEncoding;
    
    public Metadata() {
        this.modelEncoding = "UTF-8";
    }
    
    public void addPlugin(final Plugin plugin) {
        if (!(plugin instanceof Plugin)) {
            throw new ClassCastException("Metadata.addPlugins(plugin) parameter must be instanceof " + Plugin.class.getName());
        }
        this.getPlugins().add(plugin);
    }
    
    public String getArtifactId() {
        return this.artifactId;
    }
    
    public String getGroupId() {
        return this.groupId;
    }
    
    public String getModelEncoding() {
        return this.modelEncoding;
    }
    
    public List<Plugin> getPlugins() {
        if (this.plugins == null) {
            this.plugins = new ArrayList<Plugin>();
        }
        return this.plugins;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public Versioning getVersioning() {
        return this.versioning;
    }
    
    public void removePlugin(final Plugin plugin) {
        if (!(plugin instanceof Plugin)) {
            throw new ClassCastException("Metadata.removePlugins(plugin) parameter must be instanceof " + Plugin.class.getName());
        }
        this.getPlugins().remove(plugin);
    }
    
    public void setArtifactId(final String artifactId) {
        this.artifactId = artifactId;
    }
    
    public void setGroupId(final String groupId) {
        this.groupId = groupId;
    }
    
    public void setModelEncoding(final String modelEncoding) {
        this.modelEncoding = modelEncoding;
    }
    
    public void setPlugins(final List<Plugin> plugins) {
        this.plugins = plugins;
    }
    
    public void setVersion(final String version) {
        this.version = version;
    }
    
    public void setVersioning(final Versioning versioning) {
        this.versioning = versioning;
    }
    
    public boolean merge(final Metadata sourceMetadata) {
        boolean changed = false;
        for (final Plugin plugin : sourceMetadata.getPlugins()) {
            boolean found = false;
            for (Iterator it = this.getPlugins().iterator(); it.hasNext() && !found; found = true) {
                final Plugin preExisting = it.next();
                if (preExisting.getPrefix().equals(plugin.getPrefix())) {}
            }
            if (!found) {
                final Plugin mappedPlugin = new Plugin();
                mappedPlugin.setArtifactId(plugin.getArtifactId());
                mappedPlugin.setPrefix(plugin.getPrefix());
                mappedPlugin.setName(plugin.getName());
                this.addPlugin(mappedPlugin);
                changed = true;
            }
        }
        final Versioning versioning = sourceMetadata.getVersioning();
        if (versioning != null) {
            Versioning v = this.getVersioning();
            if (v == null) {
                v = new Versioning();
                this.setVersioning(v);
                changed = true;
            }
            for (final String version : versioning.getVersions()) {
                if (!v.getVersions().contains(version)) {
                    changed = true;
                    v.getVersions().add(version);
                }
            }
            if ("null".equals(versioning.getLastUpdated())) {
                versioning.setLastUpdated(null);
            }
            if ("null".equals(v.getLastUpdated())) {
                v.setLastUpdated(null);
            }
            if (versioning.getLastUpdated() == null || versioning.getLastUpdated().length() == 0) {
                versioning.setLastUpdated(v.getLastUpdated());
            }
            if (v.getLastUpdated() == null || v.getLastUpdated().length() == 0 || versioning.getLastUpdated().compareTo(v.getLastUpdated()) >= 0) {
                changed = true;
                v.setLastUpdated(versioning.getLastUpdated());
                if (versioning.getRelease() != null) {
                    changed = true;
                    v.setRelease(versioning.getRelease());
                }
                if (versioning.getLatest() != null) {
                    changed = true;
                    v.setLatest(versioning.getLatest());
                }
                Snapshot s = v.getSnapshot();
                final Snapshot snapshot = versioning.getSnapshot();
                if (snapshot != null) {
                    if (s == null) {
                        s = new Snapshot();
                        v.setSnapshot(s);
                        changed = true;
                    }
                    Label_0484: {
                        if (s.getTimestamp() == null) {
                            if (snapshot.getTimestamp() == null) {
                                break Label_0484;
                            }
                        }
                        else if (s.getTimestamp().equals(snapshot.getTimestamp())) {
                            break Label_0484;
                        }
                        s.setTimestamp(snapshot.getTimestamp());
                        changed = true;
                    }
                    if (s.getBuildNumber() != snapshot.getBuildNumber()) {
                        s.setBuildNumber(snapshot.getBuildNumber());
                        changed = true;
                    }
                    if (s.isLocalCopy() != snapshot.isLocalCopy()) {
                        s.setLocalCopy(snapshot.isLocalCopy());
                        changed = true;
                    }
                }
            }
        }
        return changed;
    }
}
