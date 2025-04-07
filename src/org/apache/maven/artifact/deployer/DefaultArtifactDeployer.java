// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.deployer;

import java.util.Iterator;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataDeploymentException;
import java.io.IOException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.codehaus.plexus.util.FileUtils;
import java.io.File;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataManager;
import org.apache.maven.artifact.transform.ArtifactTransformationManager;
import org.apache.maven.artifact.manager.WagonManager;
import org.codehaus.plexus.logging.AbstractLogEnabled;

public class DefaultArtifactDeployer extends AbstractLogEnabled implements ArtifactDeployer
{
    private WagonManager wagonManager;
    private ArtifactTransformationManager transformationManager;
    private RepositoryMetadataManager repositoryMetadataManager;
    
    @Deprecated
    public void deploy(final String basedir, final String finalName, final Artifact artifact, final ArtifactRepository deploymentRepository, final ArtifactRepository localRepository) throws ArtifactDeploymentException {
        final String extension = artifact.getArtifactHandler().getExtension();
        final File source = new File(basedir, finalName + "." + extension);
        this.deploy(source, artifact, deploymentRepository, localRepository);
    }
    
    public void deploy(File source, final Artifact artifact, final ArtifactRepository deploymentRepository, final ArtifactRepository localRepository) throws ArtifactDeploymentException {
        if (!this.wagonManager.isOnline()) {
            throw new ArtifactDeploymentException("System is offline. Cannot deploy artifact: " + artifact + ".");
        }
        boolean useArtifactFile = false;
        final File oldArtifactFile = artifact.getFile();
        if ("pom".equals(artifact.getType())) {
            artifact.setFile(source);
            useArtifactFile = true;
        }
        try {
            this.transformationManager.transformForDeployment(artifact, deploymentRepository, localRepository);
            if (useArtifactFile) {
                source = artifact.getFile();
                artifact.setFile(oldArtifactFile);
            }
            final File artifactFile = new File(localRepository.getBasedir(), localRepository.pathOf(artifact));
            if (!artifactFile.equals(source)) {
                FileUtils.copyFile(source, artifactFile);
            }
            this.wagonManager.putArtifact(source, artifact, deploymentRepository);
            for (final ArtifactMetadata metadata : artifact.getMetadataList()) {
                this.repositoryMetadataManager.deploy(metadata, localRepository, deploymentRepository);
            }
        }
        catch (TransferFailedException e) {
            throw new ArtifactDeploymentException("Error deploying artifact: " + e.getMessage(), e);
        }
        catch (IOException e2) {
            throw new ArtifactDeploymentException("Error deploying artifact: " + e2.getMessage(), e2);
        }
        catch (RepositoryMetadataDeploymentException e3) {
            throw new ArtifactDeploymentException("Error installing artifact's metadata: " + e3.getMessage(), e3);
        }
    }
}
