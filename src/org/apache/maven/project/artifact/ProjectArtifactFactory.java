// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project.artifact;

import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.apache.maven.artifact.factory.DefaultArtifactFactory;

public class ProjectArtifactFactory extends DefaultArtifactFactory
{
    public Artifact create(final MavenProject project) {
        final ArtifactHandler handler = this.getArtifactHandlerManager().getArtifactHandler(project.getPackaging());
        return new DefaultArtifact(project.getGroupId(), project.getArtifactId(), VersionRange.createFromVersion(project.getVersion()), null, project.getPackaging(), null, handler, false);
    }
    
    public Artifact create(final MavenProject project, final String type, final String classifier, final boolean optional) {
        final ArtifactHandler handler = this.getArtifactHandlerManager().getArtifactHandler(type);
        return new DefaultArtifact(project.getGroupId(), project.getArtifactId(), VersionRange.createFromVersion(project.getVersion()), null, project.getPackaging(), null, handler, optional);
    }
}
