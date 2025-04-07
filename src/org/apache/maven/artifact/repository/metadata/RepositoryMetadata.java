// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.repository.metadata;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.metadata.ArtifactMetadata;

public interface RepositoryMetadata extends ArtifactMetadata
{
    void setRepository(final ArtifactRepository p0);
    
    Metadata getMetadata();
    
    void setMetadata(final Metadata p0);
    
    boolean isSnapshot();
}
