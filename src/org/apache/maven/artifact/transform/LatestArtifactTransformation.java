// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.transform;

import org.apache.maven.artifact.repository.metadata.Versioning;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import java.util.List;
import org.apache.maven.artifact.Artifact;

public class LatestArtifactTransformation extends AbstractVersionTransformation
{
    public void transformForResolve(final Artifact artifact, final List<ArtifactRepository> remoteRepositories, final ArtifactRepository localRepository) throws ArtifactResolutionException, ArtifactNotFoundException {
        if ("LATEST".equals(artifact.getVersion())) {
            try {
                final String version = this.resolveVersion(artifact, localRepository, remoteRepositories);
                if ("LATEST".equals(version)) {
                    throw new ArtifactNotFoundException("Unable to determine the latest version", artifact);
                }
                artifact.setBaseVersion(version);
                artifact.updateVersion(version, localRepository);
            }
            catch (RepositoryMetadataResolutionException e) {
                throw new ArtifactResolutionException(e.getMessage(), artifact, e);
            }
        }
    }
    
    public void transformForInstall(final Artifact artifact, final ArtifactRepository localRepository) {
    }
    
    public void transformForDeployment(final Artifact artifact, final ArtifactRepository remoteRepository, final ArtifactRepository localRepository) {
    }
    
    @Override
    protected String constructVersion(final Versioning versioning, final String baseVersion) {
        return versioning.getLatest();
    }
}
