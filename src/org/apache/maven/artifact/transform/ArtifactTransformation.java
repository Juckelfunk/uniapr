// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.transform;

import org.apache.maven.artifact.deployer.ArtifactDeploymentException;
import org.apache.maven.artifact.installer.ArtifactInstallationException;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import java.util.List;
import org.apache.maven.artifact.Artifact;

public interface ArtifactTransformation
{
    public static final String ROLE = ArtifactTransformation.class.getName();
    
    void transformForResolve(final Artifact p0, final List<ArtifactRepository> p1, final ArtifactRepository p2) throws ArtifactResolutionException, ArtifactNotFoundException;
    
    void transformForInstall(final Artifact p0, final ArtifactRepository p1) throws ArtifactInstallationException;
    
    void transformForDeployment(final Artifact p0, final ArtifactRepository p1, final ArtifactRepository p2) throws ArtifactDeploymentException;
}
