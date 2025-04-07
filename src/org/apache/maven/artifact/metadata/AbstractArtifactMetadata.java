// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.metadata;

import org.apache.maven.artifact.Artifact;

public abstract class AbstractArtifactMetadata implements ArtifactMetadata
{
    protected Artifact artifact;
    
    protected AbstractArtifactMetadata(final Artifact artifact) {
        this.artifact = artifact;
    }
    
    public boolean storedInGroupDirectory() {
        return false;
    }
    
    public String getGroupId() {
        return this.artifact.getGroupId();
    }
    
    public String getArtifactId() {
        return this.artifact.getArtifactId();
    }
    
    public String extendedToString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("\nArtifact Metadata\n--------------------------");
        buffer.append("\nGroupId: ").append(this.getGroupId());
        buffer.append("\nArtifactId: ").append(this.getArtifactId());
        buffer.append("\nMetadata Type: ").append(this.getClass().getName());
        return buffer.toString();
    }
}
