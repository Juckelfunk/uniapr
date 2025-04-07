// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.repository;

import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.Artifact;

public interface ArtifactRepository
{
    String pathOf(final Artifact p0);
    
    String pathOfRemoteRepositoryMetadata(final ArtifactMetadata p0);
    
    String pathOfLocalRepositoryMetadata(final ArtifactMetadata p0, final ArtifactRepository p1);
    
    String getUrl();
    
    String getBasedir();
    
    String getProtocol();
    
    String getId();
    
    ArtifactRepositoryPolicy getSnapshots();
    
    ArtifactRepositoryPolicy getReleases();
    
    ArtifactRepositoryLayout getLayout();
    
    String getKey();
    
    boolean isUniqueVersion();
    
    void setBlacklisted(final boolean p0);
    
    boolean isBlacklisted();
}
