// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.repository.layout;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.Artifact;

public class DefaultRepositoryLayout implements ArtifactRepositoryLayout
{
    private static final char PATH_SEPARATOR = '/';
    private static final char GROUP_SEPARATOR = '.';
    private static final char ARTIFACT_SEPARATOR = '-';
    
    public String pathOf(final Artifact artifact) {
        final ArtifactHandler artifactHandler = artifact.getArtifactHandler();
        final StringBuffer path = new StringBuffer();
        path.append(this.formatAsDirectory(artifact.getGroupId())).append('/');
        path.append(artifact.getArtifactId()).append('/');
        path.append(artifact.getBaseVersion()).append('/');
        path.append(artifact.getArtifactId()).append('-').append(artifact.getVersion());
        if (artifact.hasClassifier()) {
            path.append('-').append(artifact.getClassifier());
        }
        if (artifactHandler.getExtension() != null && artifactHandler.getExtension().length() > 0) {
            path.append('.').append(artifactHandler.getExtension());
        }
        return path.toString();
    }
    
    public String pathOfLocalRepositoryMetadata(final ArtifactMetadata metadata, final ArtifactRepository repository) {
        return this.pathOfRepositoryMetadata(metadata, metadata.getLocalFilename(repository));
    }
    
    private String pathOfRepositoryMetadata(final ArtifactMetadata metadata, final String filename) {
        final StringBuffer path = new StringBuffer();
        path.append(this.formatAsDirectory(metadata.getGroupId())).append('/');
        if (!metadata.storedInGroupDirectory()) {
            path.append(metadata.getArtifactId()).append('/');
            if (metadata.storedInArtifactVersionDirectory()) {
                path.append(metadata.getBaseVersion()).append('/');
            }
        }
        path.append(filename);
        return path.toString();
    }
    
    public String pathOfRemoteRepositoryMetadata(final ArtifactMetadata metadata) {
        return this.pathOfRepositoryMetadata(metadata, metadata.getRemoteFilename());
    }
    
    private String formatAsDirectory(final String directory) {
        return directory.replace('.', '/');
    }
}
