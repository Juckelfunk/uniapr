// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.resolver;

import java.util.Map;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import java.util.Set;
import org.apache.maven.artifact.repository.ArtifactRepository;
import java.util.List;
import org.apache.maven.artifact.Artifact;

public interface ArtifactResolver
{
    public static final String ROLE = ArtifactResolver.class.getName();
    
    void resolve(final Artifact p0, final List p1, final ArtifactRepository p2) throws ArtifactResolutionException, ArtifactNotFoundException;
    
    ArtifactResolutionResult resolveTransitively(final Set p0, final Artifact p1, final List p2, final ArtifactRepository p3, final ArtifactMetadataSource p4) throws ArtifactResolutionException, ArtifactNotFoundException;
    
    ArtifactResolutionResult resolveTransitively(final Set p0, final Artifact p1, final List p2, final ArtifactRepository p3, final ArtifactMetadataSource p4, final List p5) throws ArtifactResolutionException, ArtifactNotFoundException;
    
    ArtifactResolutionResult resolveTransitively(final Set p0, final Artifact p1, final ArtifactRepository p2, final List p3, final ArtifactMetadataSource p4, final ArtifactFilter p5) throws ArtifactResolutionException, ArtifactNotFoundException;
    
    ArtifactResolutionResult resolveTransitively(final Set p0, final Artifact p1, final Map p2, final ArtifactRepository p3, final List p4, final ArtifactMetadataSource p5) throws ArtifactResolutionException, ArtifactNotFoundException;
    
    ArtifactResolutionResult resolveTransitively(final Set p0, final Artifact p1, final Map p2, final ArtifactRepository p3, final List p4, final ArtifactMetadataSource p5, final ArtifactFilter p6) throws ArtifactResolutionException, ArtifactNotFoundException;
    
    ArtifactResolutionResult resolveTransitively(final Set p0, final Artifact p1, final Map p2, final ArtifactRepository p3, final List p4, final ArtifactMetadataSource p5, final ArtifactFilter p6, final List p7) throws ArtifactResolutionException, ArtifactNotFoundException;
    
    void resolveAlways(final Artifact p0, final List p1, final ArtifactRepository p2) throws ArtifactResolutionException, ArtifactNotFoundException;
}
