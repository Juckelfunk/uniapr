// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.repository.metadata;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.Artifact;

public class SnapshotArtifactRepositoryMetadata extends AbstractRepositoryMetadata
{
    private Artifact artifact;
    
    public SnapshotArtifactRepositoryMetadata(final Artifact artifact) {
        super(AbstractRepositoryMetadata.createMetadata(artifact, null));
        this.artifact = artifact;
    }
    
    public SnapshotArtifactRepositoryMetadata(final Artifact artifact, final Snapshot snapshot) {
        super(AbstractRepositoryMetadata.createMetadata(artifact, AbstractRepositoryMetadata.createVersioning(snapshot)));
        this.artifact = artifact;
    }
    
    public boolean storedInGroupDirectory() {
        return false;
    }
    
    public boolean storedInArtifactVersionDirectory() {
        return true;
    }
    
    public String getGroupId() {
        return this.artifact.getGroupId();
    }
    
    public String getArtifactId() {
        return this.artifact.getArtifactId();
    }
    
    public String getBaseVersion() {
        return this.artifact.getBaseVersion();
    }
    
    public Object getKey() {
        return "snapshot " + this.artifact.getGroupId() + ":" + this.artifact.getArtifactId() + ":" + this.artifact.getBaseVersion();
    }
    
    public boolean isSnapshot() {
        return this.artifact.isSnapshot();
    }
    
    public void setRepository(final ArtifactRepository remoteRepository) {
        this.artifact.setRepository(remoteRepository);
    }
}
