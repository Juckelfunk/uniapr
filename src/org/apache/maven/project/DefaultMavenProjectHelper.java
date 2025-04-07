// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project;

import org.apache.maven.model.Resource;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.project.artifact.AttachedArtifact;
import java.io.File;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;

public class DefaultMavenProjectHelper implements MavenProjectHelper
{
    private ArtifactHandlerManager artifactHandlerManager;
    
    public void attachArtifact(final MavenProject project, final String artifactType, final String artifactClassifier, final File artifactFile) {
        final String type = artifactType;
        ArtifactHandler handler = null;
        if (type != null) {
            handler = this.artifactHandlerManager.getArtifactHandler(artifactType);
        }
        if (handler == null) {
            handler = this.artifactHandlerManager.getArtifactHandler("jar");
        }
        final Artifact artifact = new AttachedArtifact(project.getArtifact(), artifactType, artifactClassifier, handler);
        artifact.setFile(artifactFile);
        artifact.setResolved(true);
        project.addAttachedArtifact(artifact);
    }
    
    public void attachArtifact(final MavenProject project, final String artifactType, final File artifactFile) {
        final ArtifactHandler handler = this.artifactHandlerManager.getArtifactHandler(artifactType);
        final Artifact artifact = new AttachedArtifact(project.getArtifact(), artifactType, handler);
        artifact.setFile(artifactFile);
        artifact.setResolved(true);
        project.addAttachedArtifact(artifact);
    }
    
    public void attachArtifact(final MavenProject project, final File artifactFile, final String artifactClassifier) {
        final Artifact projectArtifact = project.getArtifact();
        final Artifact artifact = new AttachedArtifact(projectArtifact, projectArtifact.getType(), artifactClassifier, projectArtifact.getArtifactHandler());
        artifact.setFile(artifactFile);
        artifact.setResolved(true);
        project.addAttachedArtifact(artifact);
    }
    
    public void addResource(final MavenProject project, final String resourceDirectory, final List includes, final List excludes) {
        final Resource resource = new Resource();
        resource.setDirectory(resourceDirectory);
        resource.setIncludes(includes);
        resource.setExcludes(excludes);
        project.addResource(resource);
    }
    
    public void addTestResource(final MavenProject project, final String resourceDirectory, final List includes, final List excludes) {
        final Resource resource = new Resource();
        resource.setDirectory(resourceDirectory);
        resource.setIncludes(includes);
        resource.setExcludes(excludes);
        project.addTestResource(resource);
    }
}
