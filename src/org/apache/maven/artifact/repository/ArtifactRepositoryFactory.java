// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.repository;

import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;

public interface ArtifactRepositoryFactory
{
    public static final String ROLE = ArtifactRepositoryFactory.class.getName();
    
    ArtifactRepository createDeploymentArtifactRepository(final String p0, final String p1, final ArtifactRepositoryLayout p2, final boolean p3);
    
    ArtifactRepository createArtifactRepository(final String p0, final String p1, final ArtifactRepositoryLayout p2, final ArtifactRepositoryPolicy p3, final ArtifactRepositoryPolicy p4);
    
    void setGlobalUpdatePolicy(final String p0);
    
    void setGlobalChecksumPolicy(final String p0);
}
