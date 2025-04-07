// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.resolver;

import java.util.List;
import org.apache.maven.artifact.Artifact;

public class ArtifactNotFoundException extends AbstractArtifactResolutionException
{
    private String downloadUrl;
    
    protected ArtifactNotFoundException(final String message, final Artifact artifact, final List remoteRepositories) {
        super(message, artifact, remoteRepositories);
    }
    
    public ArtifactNotFoundException(final String message, final Artifact artifact) {
        this(message, artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), artifact.getType(), artifact.getClassifier(), null, artifact.getDownloadUrl(), artifact.getDependencyTrail());
    }
    
    protected ArtifactNotFoundException(final String message, final Artifact artifact, final List remoteRepositories, final Throwable t) {
        this(message, artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), artifact.getType(), artifact.getClassifier(), remoteRepositories, artifact.getDownloadUrl(), artifact.getDependencyTrail(), t);
    }
    
    public ArtifactNotFoundException(final String message, final String groupId, final String artifactId, final String version, final String type, final String classifier, final List remoteRepositories, final String downloadUrl, final List path, final Throwable t) {
        super(AbstractArtifactResolutionException.constructMissingArtifactMessage(message, "", groupId, artifactId, version, type, classifier, downloadUrl, path), groupId, artifactId, version, type, classifier, remoteRepositories, null, t);
        this.downloadUrl = downloadUrl;
    }
    
    private ArtifactNotFoundException(final String message, final String groupId, final String artifactId, final String version, final String type, final String classifier, final List remoteRepositories, final String downloadUrl, final List path) {
        super(AbstractArtifactResolutionException.constructMissingArtifactMessage(message, "", groupId, artifactId, version, type, classifier, downloadUrl, path), groupId, artifactId, version, type, classifier, remoteRepositories, null);
        this.downloadUrl = downloadUrl;
    }
    
    public String getDownloadUrl() {
        return this.downloadUrl;
    }
}
