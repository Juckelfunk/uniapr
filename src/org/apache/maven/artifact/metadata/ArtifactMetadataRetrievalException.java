// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.metadata;

import org.apache.maven.artifact.Artifact;

public class ArtifactMetadataRetrievalException extends Exception
{
    private Artifact artifact;
    
    @Deprecated
    public ArtifactMetadataRetrievalException(final String message) {
        this(message, null, null);
    }
    
    @Deprecated
    public ArtifactMetadataRetrievalException(final Throwable cause) {
        this(null, cause, null);
    }
    
    @Deprecated
    public ArtifactMetadataRetrievalException(final String message, final Throwable cause) {
        this(message, cause, null);
    }
    
    public ArtifactMetadataRetrievalException(final String message, final Throwable cause, final Artifact artifact) {
        super(message, cause);
        this.artifact = artifact;
    }
    
    public Artifact getArtifact() {
        return this.artifact;
    }
}
