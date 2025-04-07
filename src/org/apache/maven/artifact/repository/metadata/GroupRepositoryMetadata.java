// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.repository.metadata;

import org.apache.maven.artifact.repository.ArtifactRepository;
import java.util.Iterator;
import java.util.List;

public class GroupRepositoryMetadata extends AbstractRepositoryMetadata
{
    private final String groupId;
    
    public GroupRepositoryMetadata(final String groupId) {
        super(new Metadata());
        this.groupId = groupId;
    }
    
    public boolean storedInGroupDirectory() {
        return true;
    }
    
    public boolean storedInArtifactVersionDirectory() {
        return false;
    }
    
    public String getGroupId() {
        return this.groupId;
    }
    
    public String getArtifactId() {
        return null;
    }
    
    public String getBaseVersion() {
        return null;
    }
    
    public void addPluginMapping(final String goalPrefix, final String artifactId) {
        this.addPluginMapping(goalPrefix, artifactId, artifactId);
    }
    
    public void addPluginMapping(final String goalPrefix, final String artifactId, final String name) {
        final List plugins = this.getMetadata().getPlugins();
        boolean found = false;
        for (Iterator i = plugins.iterator(); i.hasNext() && !found; found = true) {
            final Plugin plugin = i.next();
            if (plugin.getPrefix().equals(goalPrefix)) {}
        }
        if (!found) {
            final Plugin plugin2 = new Plugin();
            plugin2.setPrefix(goalPrefix);
            plugin2.setArtifactId(artifactId);
            plugin2.setName(name);
            this.getMetadata().addPlugin(plugin2);
        }
    }
    
    public Object getKey() {
        return this.groupId;
    }
    
    public boolean isSnapshot() {
        return false;
    }
    
    public void setRepository(final ArtifactRepository remoteRepository) {
    }
}
