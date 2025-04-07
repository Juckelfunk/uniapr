// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.resolver;

import java.util.Iterator;
import java.util.ArrayList;
import org.apache.maven.artifact.Artifact;
import java.util.List;

public class MultipleArtifactsNotFoundException extends ArtifactResolutionException
{
    private final List resolvedArtifacts;
    private final List missingArtifacts;
    
    @Deprecated
    public MultipleArtifactsNotFoundException(final Artifact originatingArtifact, final List missingArtifacts, final List remoteRepositories) {
        this(originatingArtifact, new ArrayList(), missingArtifacts, remoteRepositories);
    }
    
    public MultipleArtifactsNotFoundException(final Artifact originatingArtifact, final List resolvedArtifacts, final List missingArtifacts, final List remoteRepositories) {
        super(constructMessage(missingArtifacts), originatingArtifact, remoteRepositories);
        this.resolvedArtifacts = resolvedArtifacts;
        this.missingArtifacts = missingArtifacts;
    }
    
    public List getResolvedArtifacts() {
        return this.resolvedArtifacts;
    }
    
    public List getMissingArtifacts() {
        return this.missingArtifacts;
    }
    
    private static String constructMessage(final List artifacts) {
        final StringBuffer buffer = new StringBuffer("Missing:\n");
        buffer.append("----------\n");
        int counter = 0;
        for (final Artifact artifact : artifacts) {
            final String message = ++counter + ") " + artifact.getId();
            buffer.append(AbstractArtifactResolutionException.constructMissingArtifactMessage(message, "  ", artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), artifact.getType(), artifact.getClassifier(), artifact.getDownloadUrl(), artifact.getDependencyTrail()));
        }
        buffer.append("----------\n");
        final int size = artifacts.size();
        buffer.append(size).append(" required artifact");
        if (size > 1) {
            buffer.append("s are");
        }
        else {
            buffer.append(" is");
        }
        buffer.append(" missing.\n\nfor artifact: ");
        return buffer.toString();
    }
}
