// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project.artifact;

import java.io.IOException;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataStoreException;
import org.codehaus.plexus.util.FileUtils;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.Artifact;
import java.io.File;
import org.apache.maven.artifact.metadata.AbstractArtifactMetadata;

public class ProjectArtifactMetadata extends AbstractArtifactMetadata
{
    private File originalFile;
    private File transformedFile;
    private boolean versionExpressionsResolved;
    
    public ProjectArtifactMetadata(final Artifact artifact) {
        this(artifact, null);
    }
    
    public ProjectArtifactMetadata(final Artifact artifact, final File file) {
        super(artifact);
        this.versionExpressionsResolved = false;
        this.originalFile = file;
    }
    
    public String getRemoteFilename() {
        return this.getFilename();
    }
    
    public String getLocalFilename(final ArtifactRepository repository) {
        return this.getFilename();
    }
    
    private String getFilename() {
        return this.getArtifactId() + "-" + this.artifact.getVersion() + ".pom";
    }
    
    public void storeInLocalRepository(final ArtifactRepository localRepository, final ArtifactRepository remoteRepository) throws RepositoryMetadataStoreException {
        final File f = (this.transformedFile == null) ? this.originalFile : this.transformedFile;
        if (f == null) {
            return;
        }
        final File destination = new File(localRepository.getBasedir(), localRepository.pathOfLocalRepositoryMetadata(this, remoteRepository));
        try {
            FileUtils.copyFile(f, destination);
        }
        catch (IOException e) {
            throw new RepositoryMetadataStoreException("Error copying POM to the local repository.", e);
        }
    }
    
    @Override
    public String toString() {
        return "project information for " + this.artifact.getArtifactId() + " " + this.artifact.getVersion();
    }
    
    public boolean storedInArtifactVersionDirectory() {
        return true;
    }
    
    public String getBaseVersion() {
        return this.artifact.getBaseVersion();
    }
    
    public Object getKey() {
        return "project " + this.artifact.getGroupId() + ":" + this.artifact.getArtifactId();
    }
    
    public void merge(final ArtifactMetadata metadata) {
        final ProjectArtifactMetadata m = (ProjectArtifactMetadata)metadata;
        if (!m.originalFile.equals(this.originalFile)) {
            throw new IllegalStateException("Cannot add two different pieces of metadata for: " + this.getKey());
        }
    }
    
    public boolean isVersionExpressionsResolved() {
        return this.versionExpressionsResolved;
    }
    
    public void setVersionExpressionsResolved(final boolean versionExpressionsResolved) {
        this.versionExpressionsResolved = versionExpressionsResolved;
    }
    
    public void setFile(final File file) {
        this.transformedFile = file;
    }
    
    public File getFile() {
        return (this.transformedFile == null) ? this.originalFile : this.transformedFile;
    }
}
