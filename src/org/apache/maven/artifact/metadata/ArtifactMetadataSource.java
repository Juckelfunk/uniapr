// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.metadata;

import java.util.List;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.Artifact;

public interface ArtifactMetadataSource
{
    public static final String ROLE = ArtifactMetadataSource.class.getName();
    
    ResolutionGroup retrieve(final Artifact p0, final ArtifactRepository p1, final List p2) throws ArtifactMetadataRetrievalException;
    
    Artifact retrieveRelocatedArtifact(final Artifact p0, final ArtifactRepository p1, final List p2) throws ArtifactMetadataRetrievalException;
    
    List retrieveAvailableVersions(final Artifact p0, final ArtifactRepository p1, final List p2) throws ArtifactMetadataRetrievalException;
}
