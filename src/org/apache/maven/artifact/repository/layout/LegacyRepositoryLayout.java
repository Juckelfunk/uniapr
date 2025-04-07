// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.repository.layout;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.Artifact;

public class LegacyRepositoryLayout implements ArtifactRepositoryLayout
{
    private static final String PATH_SEPARATOR = "/";
    
    public String pathOf(final Artifact artifact) {
        final ArtifactHandler artifactHandler = artifact.getArtifactHandler();
        final StringBuffer path = new StringBuffer();
        path.append(artifact.getGroupId()).append('/');
        path.append(artifactHandler.getDirectory()).append('/');
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
        path.append(metadata.getGroupId()).append("/").append("poms").append("/");
        path.append(filename);
        return path.toString();
    }
    
    public String pathOfRemoteRepositoryMetadata(final ArtifactMetadata metadata) {
        return this.pathOfRepositoryMetadata(metadata, metadata.getRemoteFilename());
    }
}
