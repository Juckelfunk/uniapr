// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.repository.metadata;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.Artifact;

public class ArtifactRepositoryMetadata extends AbstractRepositoryMetadata
{
    private Artifact artifact;
    
    public ArtifactRepositoryMetadata(final Artifact artifact) {
        this(artifact, null);
    }
    
    public ArtifactRepositoryMetadata(final Artifact artifact, final Versioning versioning) {
        super(AbstractRepositoryMetadata.createMetadata(artifact, versioning));
        this.artifact = artifact;
    }
    
    public boolean storedInGroupDirectory() {
        return false;
    }
    
    public boolean storedInArtifactVersionDirectory() {
        return false;
    }
    
    public String getGroupId() {
        return this.artifact.getGroupId();
    }
    
    public String getArtifactId() {
        return this.artifact.getArtifactId();
    }
    
    public String getBaseVersion() {
        return null;
    }
    
    public Object getKey() {
        return "artifact " + this.artifact.getGroupId() + ":" + this.artifact.getArtifactId();
    }
    
    public boolean isSnapshot() {
        return false;
    }
    
    public void setRepository(final ArtifactRepository remoteRepository) {
        this.artifact.setRepository(remoteRepository);
    }
}
