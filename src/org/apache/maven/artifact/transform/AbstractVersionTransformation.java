// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.transform;

import org.apache.maven.artifact.repository.metadata.Versioning;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataResolutionException;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadata;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.metadata.SnapshotArtifactRepositoryMetadata;
import org.apache.maven.artifact.repository.metadata.ArtifactRepositoryMetadata;
import java.util.List;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataManager;
import org.codehaus.plexus.logging.AbstractLogEnabled;

public abstract class AbstractVersionTransformation extends AbstractLogEnabled implements ArtifactTransformation
{
    protected RepositoryMetadataManager repositoryMetadataManager;
    protected WagonManager wagonManager;
    
    protected String resolveVersion(final Artifact artifact, final ArtifactRepository localRepository, final List<ArtifactRepository> remoteRepositories) throws RepositoryMetadataResolutionException {
        RepositoryMetadata metadata;
        if (!artifact.isSnapshot() || "LATEST".equals(artifact.getBaseVersion())) {
            metadata = new ArtifactRepositoryMetadata(artifact);
        }
        else {
            metadata = new SnapshotArtifactRepositoryMetadata(artifact);
        }
        this.repositoryMetadataManager.resolve(metadata, remoteRepositories, localRepository);
        artifact.addMetadata(metadata);
        final Metadata repoMetadata = metadata.getMetadata();
        String version = null;
        if (repoMetadata != null && repoMetadata.getVersioning() != null) {
            version = this.constructVersion(repoMetadata.getVersioning(), artifact.getBaseVersion());
        }
        if (version == null) {
            version = artifact.getBaseVersion();
        }
        if (this.getLogger().isDebugEnabled()) {
            if (!version.equals(artifact.getBaseVersion())) {
                String message = artifact.getArtifactId() + ": resolved to version " + version;
                if (artifact.getRepository() != null) {
                    message = message + " from repository " + artifact.getRepository().getId();
                }
                else {
                    message += " from local repository";
                }
                this.getLogger().debug(message);
            }
            else {
                this.getLogger().debug(artifact.getArtifactId() + ": using locally installed snapshot");
            }
        }
        return version;
    }
    
    protected abstract String constructVersion(final Versioning p0, final String p1);
}
