// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.deployer;

import java.io.File;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.Artifact;

public interface ArtifactDeployer
{
    public static final String ROLE = ArtifactDeployer.class.getName();
    
    @Deprecated
    void deploy(final String p0, final String p1, final Artifact p2, final ArtifactRepository p3, final ArtifactRepository p4) throws ArtifactDeploymentException;
    
    void deploy(final File p0, final Artifact p1, final ArtifactRepository p2, final ArtifactRepository p3) throws ArtifactDeploymentException;
}
