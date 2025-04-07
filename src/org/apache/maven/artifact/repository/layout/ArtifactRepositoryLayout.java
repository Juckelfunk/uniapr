// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.repository.layout;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.Artifact;

public interface ArtifactRepositoryLayout
{
    public static final String ROLE = ArtifactRepositoryLayout.class.getName();
    
    String pathOf(final Artifact p0);
    
    String pathOfLocalRepositoryMetadata(final ArtifactMetadata p0, final ArtifactRepository p1);
    
    String pathOfRemoteRepositoryMetadata(final ArtifactMetadata p0);
}
