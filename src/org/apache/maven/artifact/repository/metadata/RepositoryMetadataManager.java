// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.repository.metadata;

import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;
import java.util.List;

public interface RepositoryMetadataManager
{
    void resolve(final RepositoryMetadata p0, final List p1, final ArtifactRepository p2) throws RepositoryMetadataResolutionException;
    
    void resolveAlways(final RepositoryMetadata p0, final ArtifactRepository p1, final ArtifactRepository p2) throws RepositoryMetadataResolutionException;
    
    void deploy(final ArtifactMetadata p0, final ArtifactRepository p1, final ArtifactRepository p2) throws RepositoryMetadataDeploymentException;
    
    void install(final ArtifactMetadata p0, final ArtifactRepository p1) throws RepositoryMetadataInstallationException;
}
