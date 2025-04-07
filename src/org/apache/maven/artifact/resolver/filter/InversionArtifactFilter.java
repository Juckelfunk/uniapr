// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.resolver.filter;

import org.apache.maven.artifact.Artifact;

public class InversionArtifactFilter implements ArtifactFilter
{
    private final ArtifactFilter toInvert;
    
    public InversionArtifactFilter(final ArtifactFilter toInvert) {
        this.toInvert = toInvert;
    }
    
    public boolean include(final Artifact artifact) {
        return !this.toInvert.include(artifact);
    }
}
