// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.resolver;

import org.apache.maven.artifact.Artifact;
import java.util.List;

public class ArtifactResolutionException extends AbstractArtifactResolutionException
{
    public ArtifactResolutionException(final String message, final String groupId, final String artifactId, final String version, final String type, final String classifier, final List remoteRepositories, final List path, final Throwable t) {
        super(message, groupId, artifactId, version, type, classifier, remoteRepositories, path, t);
    }
    
    public ArtifactResolutionException(final String message, final String groupId, final String artifactId, final String version, final String type, final String classifier, final Throwable t) {
        super(message, groupId, artifactId, version, type, classifier, null, null, t);
    }
    
    public ArtifactResolutionException(final String message, final Artifact artifact) {
        super(message, artifact);
    }
    
    public ArtifactResolutionException(final String message, final Artifact artifact, final List remoteRepositories) {
        super(message, artifact, remoteRepositories);
    }
    
    public ArtifactResolutionException(final String message, final Artifact artifact, final Throwable t) {
        super(message, artifact, null, t);
    }
    
    protected ArtifactResolutionException(final String message, final Artifact artifact, final List remoteRepositories, final Throwable t) {
        super(message, artifact, remoteRepositories, t);
    }
}
