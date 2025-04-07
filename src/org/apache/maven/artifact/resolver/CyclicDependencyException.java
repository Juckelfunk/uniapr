// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.resolver;

import org.apache.maven.artifact.Artifact;

public class CyclicDependencyException extends ArtifactResolutionException
{
    private Artifact artifact;
    
    public CyclicDependencyException(final String message, final Artifact artifact) {
        super(message, artifact);
        this.artifact = artifact;
    }
    
    @Override
    public Artifact getArtifact() {
        return this.artifact;
    }
}
