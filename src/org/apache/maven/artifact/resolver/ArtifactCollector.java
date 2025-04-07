// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.resolver;

import java.util.Map;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import java.util.List;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.Artifact;
import java.util.Set;

public interface ArtifactCollector
{
    ArtifactResolutionResult collect(final Set p0, final Artifact p1, final ArtifactRepository p2, final List p3, final ArtifactMetadataSource p4, final ArtifactFilter p5, final List p6) throws ArtifactResolutionException;
    
    ArtifactResolutionResult collect(final Set p0, final Artifact p1, final Map p2, final ArtifactRepository p3, final List p4, final ArtifactMetadataSource p5, final ArtifactFilter p6, final List p7) throws ArtifactResolutionException;
}
