// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.versioning;

import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;

public class OverConstrainedVersionException extends ArtifactResolutionException
{
    public OverConstrainedVersionException(final String msg, final Artifact artifact) {
        super(msg, artifact);
    }
    
    public OverConstrainedVersionException(final String msg, final Artifact artifact, final List remoteRepositories) {
        super(msg, artifact, remoteRepositories);
    }
}
