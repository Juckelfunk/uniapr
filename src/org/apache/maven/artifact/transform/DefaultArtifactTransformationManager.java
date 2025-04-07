// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.transform;

import org.apache.maven.artifact.deployer.ArtifactDeploymentException;
import org.apache.maven.artifact.installer.ArtifactInstallationException;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import java.util.Iterator;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.Artifact;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import java.util.List;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

public class DefaultArtifactTransformationManager implements ArtifactTransformationManager, Initializable
{
    private List<ArtifactTransformation> artifactTransformations;
    
    public void initialize() throws InitializationException {
        final ArtifactTransformation[] transforms = this.artifactTransformations.toArray(new ArtifactTransformation[0]);
        for (int x = 0; x < transforms.length; ++x) {
            if (transforms[x].getClass().getName().indexOf("Snapshot") != -1) {
                this.artifactTransformations.remove(transforms[x]);
                this.artifactTransformations.add(transforms[x]);
            }
        }
    }
    
    public void transformForResolve(final Artifact artifact, final List<ArtifactRepository> remoteRepositories, final ArtifactRepository localRepository) throws ArtifactResolutionException, ArtifactNotFoundException {
        for (final ArtifactTransformation transform : this.artifactTransformations) {
            transform.transformForResolve(artifact, remoteRepositories, localRepository);
        }
    }
    
    public void transformForInstall(final Artifact artifact, final ArtifactRepository localRepository) throws ArtifactInstallationException {
        for (final ArtifactTransformation transform : this.artifactTransformations) {
            transform.transformForInstall(artifact, localRepository);
        }
    }
    
    public void transformForDeployment(final Artifact artifact, final ArtifactRepository remoteRepository, final ArtifactRepository localRepository) throws ArtifactDeploymentException {
        for (final ArtifactTransformation transform : this.artifactTransformations) {
            transform.transformForDeployment(artifact, remoteRepository, localRepository);
        }
    }
}
