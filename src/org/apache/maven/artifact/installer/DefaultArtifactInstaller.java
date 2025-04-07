// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.installer;

import java.util.Iterator;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataInstallationException;
import java.io.IOException;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.codehaus.plexus.util.FileUtils;
import java.io.File;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataManager;
import org.apache.maven.artifact.transform.ArtifactTransformationManager;
import org.codehaus.plexus.logging.AbstractLogEnabled;

public class DefaultArtifactInstaller extends AbstractLogEnabled implements ArtifactInstaller
{
    private ArtifactTransformationManager transformationManager;
    private RepositoryMetadataManager repositoryMetadataManager;
    
    @Deprecated
    public void install(final String basedir, final String finalName, final Artifact artifact, final ArtifactRepository localRepository) throws ArtifactInstallationException {
        final String extension = artifact.getArtifactHandler().getExtension();
        final File source = new File(basedir, finalName + "." + extension);
        this.install(source, artifact, localRepository);
    }
    
    public void install(File source, final Artifact artifact, final ArtifactRepository localRepository) throws ArtifactInstallationException {
        boolean useArtifactFile = false;
        final File oldArtifactFile = artifact.getFile();
        if ("pom".equals(artifact.getType())) {
            artifact.setFile(source);
            useArtifactFile = true;
        }
        try {
            this.transformationManager.transformForInstall(artifact, localRepository);
            if (useArtifactFile) {
                source = artifact.getFile();
                artifact.setFile(oldArtifactFile);
            }
            final String localPath = localRepository.pathOf(artifact);
            final File destination = new File(localRepository.getBasedir(), localPath);
            if (!destination.getParentFile().exists()) {
                destination.getParentFile().mkdirs();
            }
            this.getLogger().info("Installing " + source.getPath() + " to " + destination);
            FileUtils.copyFile(source, destination);
            if (useArtifactFile) {
                artifact.setFile(destination);
            }
            for (final ArtifactMetadata metadata : artifact.getMetadataList()) {
                this.repositoryMetadataManager.install(metadata, localRepository);
            }
        }
        catch (IOException e) {
            throw new ArtifactInstallationException("Error installing artifact: " + e.getMessage(), e);
        }
        catch (RepositoryMetadataInstallationException e2) {
            throw new ArtifactInstallationException("Error installing artifact's metadata: " + e2.getMessage(), e2);
        }
    }
}
