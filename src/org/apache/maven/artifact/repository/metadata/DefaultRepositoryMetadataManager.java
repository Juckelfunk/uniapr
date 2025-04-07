// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.repository.metadata;

import java.io.Reader;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import java.io.IOException;
import java.io.FileNotFoundException;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Reader;
import org.codehaus.plexus.util.ReaderFactory;
import java.util.Map;
import java.util.HashMap;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import java.util.Iterator;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import java.util.Date;
import java.io.File;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import org.apache.maven.artifact.manager.WagonManager;
import org.codehaus.plexus.logging.AbstractLogEnabled;

public class DefaultRepositoryMetadataManager extends AbstractLogEnabled implements RepositoryMetadataManager
{
    private WagonManager wagonManager;
    private Set cachedMetadata;
    
    public DefaultRepositoryMetadataManager() {
        this.cachedMetadata = new HashSet();
    }
    
    public void resolve(final RepositoryMetadata metadata, final List remoteRepositories, final ArtifactRepository localRepository) throws RepositoryMetadataResolutionException {
        final boolean alreadyResolved = this.alreadyResolved(metadata);
        if (!alreadyResolved) {
            for (final ArtifactRepository repository : remoteRepositories) {
                final ArtifactRepositoryPolicy policy = metadata.isSnapshot() ? repository.getSnapshots() : repository.getReleases();
                if (!policy.isEnabled()) {
                    this.getLogger().debug("Skipping disabled repository " + repository.getId());
                }
                else if (repository.isBlacklisted()) {
                    this.getLogger().debug("Skipping blacklisted repository " + repository.getId());
                }
                else {
                    final File file = new File(localRepository.getBasedir(), localRepository.pathOfLocalRepositoryMetadata(metadata, repository));
                    final boolean checkForUpdates = !file.exists() || policy.checkOutOfDate(new Date(file.lastModified()));
                    if (!checkForUpdates) {
                        continue;
                    }
                    if (this.wagonManager.isOnline()) {
                        this.getLogger().info(metadata.getKey() + ": checking for updates from " + repository.getId());
                        boolean storeMetadata = false;
                        try {
                            this.wagonManager.getArtifactMetadata(metadata, repository, file, policy.getChecksumPolicy());
                            storeMetadata = true;
                        }
                        catch (ResourceDoesNotExistException e5) {
                            this.getLogger().debug(metadata + " could not be found on repository: " + repository.getId());
                            if (file.exists()) {
                                file.delete();
                            }
                            storeMetadata = true;
                        }
                        catch (TransferFailedException e) {
                            this.getLogger().warn(metadata + " could not be retrieved from repository: " + repository.getId() + " due to an error: " + e.getMessage());
                            this.getLogger().debug("Exception", e);
                            this.getLogger().info("Repository '" + repository.getId() + "' will be blacklisted");
                            repository.setBlacklisted(true);
                        }
                        if (!storeMetadata) {
                            continue;
                        }
                        if (file.exists()) {
                            file.setLastModified(System.currentTimeMillis());
                        }
                        else {
                            try {
                                metadata.storeInLocalRepository(localRepository, repository);
                            }
                            catch (RepositoryMetadataStoreException e2) {
                                throw new RepositoryMetadataResolutionException("Unable to store local copy of metadata: " + e2.getMessage(), e2);
                            }
                        }
                    }
                    else {
                        this.getLogger().debug("System is offline. Cannot resolve metadata:\n" + metadata.extendedToString() + "\n\n");
                    }
                }
            }
            this.cachedMetadata.add(metadata.getKey());
        }
        try {
            this.mergeMetadata(metadata, remoteRepositories, localRepository);
        }
        catch (RepositoryMetadataStoreException e3) {
            throw new RepositoryMetadataResolutionException("Unable to store local copy of metadata: " + e3.getMessage(), e3);
        }
        catch (RepositoryMetadataReadException e4) {
            throw new RepositoryMetadataResolutionException("Unable to read local copy of metadata: " + e4.getMessage(), e4);
        }
    }
    
    private void mergeMetadata(final RepositoryMetadata metadata, final List remoteRepositories, final ArtifactRepository localRepository) throws RepositoryMetadataStoreException, RepositoryMetadataReadException {
        final Map previousMetadata = new HashMap();
        ArtifactRepository selected = null;
        for (final ArtifactRepository repository : remoteRepositories) {
            final ArtifactRepositoryPolicy policy = metadata.isSnapshot() ? repository.getSnapshots() : repository.getReleases();
            if (policy.isEnabled() && this.loadMetadata(metadata, repository, localRepository, previousMetadata)) {
                metadata.setRepository(repository);
                selected = repository;
            }
        }
        if (this.loadMetadata(metadata, localRepository, localRepository, previousMetadata)) {
            metadata.setRepository(null);
            selected = localRepository;
        }
        this.updateSnapshotMetadata(metadata, previousMetadata, selected, localRepository);
    }
    
    private void updateSnapshotMetadata(final RepositoryMetadata metadata, final Map previousMetadata, final ArtifactRepository selected, final ArtifactRepository localRepository) throws RepositoryMetadataStoreException {
        if (metadata.isSnapshot()) {
            final Metadata prevMetadata = metadata.getMetadata();
            for (final ArtifactRepository repository : previousMetadata.keySet()) {
                final Metadata m = previousMetadata.get(repository);
                if (repository.equals(selected)) {
                    if (m.getVersioning() == null) {
                        m.setVersioning(new Versioning());
                    }
                    if (m.getVersioning().getSnapshot() != null) {
                        continue;
                    }
                    m.getVersioning().setSnapshot(new Snapshot());
                }
                else {
                    if (m.getVersioning() == null || m.getVersioning().getSnapshot() == null || !m.getVersioning().getSnapshot().isLocalCopy()) {
                        continue;
                    }
                    m.getVersioning().getSnapshot().setLocalCopy(false);
                    metadata.setMetadata(m);
                    metadata.storeInLocalRepository(localRepository, repository);
                }
            }
            metadata.setMetadata(prevMetadata);
        }
    }
    
    private boolean loadMetadata(final RepositoryMetadata repoMetadata, final ArtifactRepository remoteRepository, final ArtifactRepository localRepository, final Map previousMetadata) throws RepositoryMetadataReadException {
        boolean setRepository = false;
        final File metadataFile = new File(localRepository.getBasedir(), localRepository.pathOfLocalRepositoryMetadata(repoMetadata, remoteRepository));
        if (metadataFile.exists()) {
            final Metadata metadata = readMetadata(metadataFile);
            if (repoMetadata.isSnapshot() && previousMetadata != null) {
                previousMetadata.put(remoteRepository, metadata);
            }
            if (repoMetadata.getMetadata() != null) {
                setRepository = repoMetadata.getMetadata().merge(metadata);
            }
            else {
                repoMetadata.setMetadata(metadata);
                setRepository = true;
            }
        }
        return setRepository;
    }
    
    protected static Metadata readMetadata(final File mappingFile) throws RepositoryMetadataReadException {
        Reader reader = null;
        Metadata result;
        try {
            reader = ReaderFactory.newXmlReader(mappingFile);
            final MetadataXpp3Reader mappingReader = new MetadataXpp3Reader();
            result = mappingReader.read(reader, false);
        }
        catch (FileNotFoundException e) {
            throw new RepositoryMetadataReadException("Cannot read metadata from '" + mappingFile + "'", e);
        }
        catch (IOException e2) {
            throw new RepositoryMetadataReadException("Cannot read metadata from '" + mappingFile + "': " + e2.getMessage(), e2);
        }
        catch (XmlPullParserException e3) {
            throw new RepositoryMetadataReadException("Cannot read metadata from '" + mappingFile + "': " + e3.getMessage(), e3);
        }
        finally {
            IOUtil.close(reader);
        }
        return result;
    }
    
    public void resolveAlways(final RepositoryMetadata metadata, final ArtifactRepository localRepository, final ArtifactRepository remoteRepository) throws RepositoryMetadataResolutionException {
        if (!this.wagonManager.isOnline()) {
            throw new RepositoryMetadataResolutionException("System is offline. Cannot resolve required metadata:\n" + metadata.extendedToString());
        }
        File file;
        try {
            file = this.getArtifactMetadataFromDeploymentRepository(metadata, localRepository, remoteRepository);
        }
        catch (TransferFailedException e) {
            throw new RepositoryMetadataResolutionException(metadata + " could not be retrieved from repository: " + remoteRepository.getId() + " due to an error: " + e.getMessage(), e);
        }
        try {
            if (file.exists()) {
                final Metadata prevMetadata = readMetadata(file);
                metadata.setMetadata(prevMetadata);
            }
        }
        catch (RepositoryMetadataReadException e2) {
            throw new RepositoryMetadataResolutionException(e2.getMessage(), e2);
        }
    }
    
    private File getArtifactMetadataFromDeploymentRepository(final ArtifactMetadata metadata, final ArtifactRepository localRepository, final ArtifactRepository remoteRepository) throws TransferFailedException {
        final File file = new File(localRepository.getBasedir(), localRepository.pathOfLocalRepositoryMetadata(metadata, remoteRepository));
        try {
            this.wagonManager.getArtifactMetadataFromDeploymentRepository(metadata, remoteRepository, file, "warn");
        }
        catch (ResourceDoesNotExistException e) {
            this.getLogger().info(metadata + " could not be found on repository: " + remoteRepository.getId() + ", so will be created");
            if (file.exists()) {
                file.delete();
            }
        }
        return file;
    }
    
    private boolean alreadyResolved(final ArtifactMetadata metadata) {
        return this.cachedMetadata.contains(metadata.getKey());
    }
    
    public void deploy(final ArtifactMetadata metadata, final ArtifactRepository localRepository, final ArtifactRepository deploymentRepository) throws RepositoryMetadataDeploymentException {
        if (!this.wagonManager.isOnline()) {
            throw new RepositoryMetadataDeploymentException("System is offline. Cannot deploy metadata:\n" + metadata.extendedToString());
        }
        File file = null;
        Label_0174: {
            if (metadata instanceof RepositoryMetadata) {
                this.getLogger().info("Retrieving previous metadata from " + deploymentRepository.getId());
                try {
                    file = this.getArtifactMetadataFromDeploymentRepository(metadata, localRepository, deploymentRepository);
                    break Label_0174;
                }
                catch (TransferFailedException e) {
                    throw new RepositoryMetadataDeploymentException(metadata + " could not be retrieved from repository: " + deploymentRepository.getId() + " due to an error: " + e.getMessage(), e);
                }
            }
            file = new File(localRepository.getBasedir(), localRepository.pathOfLocalRepositoryMetadata(metadata, deploymentRepository));
            try {
                metadata.storeInLocalRepository(localRepository, deploymentRepository);
            }
            catch (RepositoryMetadataStoreException e2) {
                throw new RepositoryMetadataDeploymentException("Error installing metadata: " + e2.getMessage(), e2);
            }
        }
        try {
            this.wagonManager.putArtifactMetadata(file, metadata, deploymentRepository);
        }
        catch (TransferFailedException e) {
            throw new RepositoryMetadataDeploymentException("Error while deploying metadata: " + e.getMessage(), e);
        }
    }
    
    public void install(final ArtifactMetadata metadata, final ArtifactRepository localRepository) throws RepositoryMetadataInstallationException {
        try {
            metadata.storeInLocalRepository(localRepository, localRepository);
        }
        catch (RepositoryMetadataStoreException e) {
            throw new RepositoryMetadataInstallationException("Error installing metadata: " + e.getMessage(), e);
        }
    }
}
