// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.metadata;

import org.apache.maven.artifact.repository.metadata.RepositoryMetadataStoreException;
import org.apache.maven.artifact.repository.ArtifactRepository;

public interface ArtifactMetadata
{
    boolean storedInArtifactVersionDirectory();
    
    boolean storedInGroupDirectory();
    
    String getGroupId();
    
    String getArtifactId();
    
    String getBaseVersion();
    
    Object getKey();
    
    String getLocalFilename(final ArtifactRepository p0);
    
    String getRemoteFilename();
    
    void merge(final ArtifactMetadata p0);
    
    void storeInLocalRepository(final ArtifactRepository p0, final ArtifactRepository p1) throws RepositoryMetadataStoreException;
    
    String extendedToString();
}
