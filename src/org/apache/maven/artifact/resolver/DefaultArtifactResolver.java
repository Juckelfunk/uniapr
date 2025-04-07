// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.resolver;

import org.codehaus.plexus.logging.Logger;
import java.util.HashMap;
import edu.emory.mathcs.backport.java.util.concurrent.CountDownLatch;
import java.util.Map;
import java.util.Collections;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import java.util.Set;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.metadata.Snapshot;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.apache.maven.artifact.repository.metadata.Metadata;
import java.util.Iterator;
import java.io.IOException;
import org.codehaus.plexus.util.FileUtils;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Date;
import org.apache.maven.artifact.repository.metadata.SnapshotArtifactRepositoryMetadata;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import java.io.File;
import org.apache.maven.artifact.repository.ArtifactRepository;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import edu.emory.mathcs.backport.java.util.concurrent.ThreadPoolExecutor;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.transform.ArtifactTransformationManager;
import org.apache.maven.artifact.manager.WagonManager;
import org.codehaus.plexus.logging.AbstractLogEnabled;

public class DefaultArtifactResolver extends AbstractLogEnabled implements ArtifactResolver
{
    private static final int DEFAULT_POOL_SIZE = 5;
    private WagonManager wagonManager;
    private ArtifactTransformationManager transformationManager;
    protected ArtifactFactory artifactFactory;
    private ArtifactCollector artifactCollector;
    private final ThreadPoolExecutor resolveArtifactPool;
    
    public DefaultArtifactResolver() {
        this.resolveArtifactPool = new ThreadPoolExecutor(5, 5, 3L, TimeUnit.SECONDS, new LinkedBlockingQueue());
    }
    
    public void resolve(final Artifact artifact, final List remoteRepositories, final ArtifactRepository localRepository) throws ArtifactResolutionException, ArtifactNotFoundException {
        this.resolve(artifact, remoteRepositories, localRepository, false);
    }
    
    public void resolveAlways(final Artifact artifact, final List remoteRepositories, final ArtifactRepository localRepository) throws ArtifactResolutionException, ArtifactNotFoundException {
        this.resolve(artifact, remoteRepositories, localRepository, true);
    }
    
    private void resolve(final Artifact artifact, final List remoteRepositories, final ArtifactRepository localRepository, boolean force) throws ArtifactResolutionException, ArtifactNotFoundException {
        if (artifact == null) {
            return;
        }
        if ("system".equals(artifact.getScope())) {
            final File systemFile = artifact.getFile();
            if (systemFile == null) {
                throw new ArtifactNotFoundException("System artifact: " + artifact + " has no file attached", artifact);
            }
            if (!systemFile.isFile()) {
                throw new ArtifactNotFoundException("System artifact: " + artifact + " is not a file: " + systemFile, artifact);
            }
            if (!systemFile.exists()) {
                throw new ArtifactNotFoundException("System artifact: " + artifact + " not found in path: " + systemFile, artifact);
            }
            artifact.setResolved(true);
        }
        else if (!artifact.isResolved()) {
            final String localPath = localRepository.pathOf(artifact);
            artifact.setFile(new File(localRepository.getBasedir(), localPath));
            this.transformationManager.transformForResolve(artifact, remoteRepositories, localRepository);
            boolean localCopy = false;
            for (final ArtifactMetadata m : artifact.getMetadataList()) {
                if (m instanceof SnapshotArtifactRepositoryMetadata) {
                    final SnapshotArtifactRepositoryMetadata snapshotMetadata = (SnapshotArtifactRepositoryMetadata)m;
                    final Metadata metadata = snapshotMetadata.getMetadata();
                    if (metadata == null) {
                        continue;
                    }
                    final Versioning versioning = metadata.getVersioning();
                    if (versioning == null) {
                        continue;
                    }
                    final Snapshot snapshot = versioning.getSnapshot();
                    if (snapshot == null) {
                        continue;
                    }
                    localCopy = snapshot.isLocalCopy();
                }
            }
            final File destination = artifact.getFile();
            List repositories = remoteRepositories;
            if (artifact.isSnapshot() && artifact.getBaseVersion().equals(artifact.getVersion()) && destination.exists() && !localCopy && this.wagonManager.isOnline()) {
                final Date comparisonDate = new Date(destination.lastModified());
                repositories = new ArrayList(remoteRepositories);
                final Iterator j = repositories.iterator();
                while (j.hasNext()) {
                    final ArtifactRepository repository = j.next();
                    final ArtifactRepositoryPolicy policy = repository.getSnapshots();
                    if (!policy.isEnabled() || !policy.checkOutOfDate(comparisonDate)) {
                        j.remove();
                    }
                }
                if (!repositories.isEmpty()) {
                    force = true;
                }
            }
            Label_0659: {
                if (!destination.exists() || force) {
                    if (!this.wagonManager.isOnline()) {
                        throw new ArtifactNotFoundException("System is offline.", artifact);
                    }
                    try {
                        if (artifact.getRepository() != null) {
                            this.wagonManager.getArtifact(artifact, artifact.getRepository());
                        }
                        else {
                            this.wagonManager.getArtifact(artifact, repositories);
                        }
                        if (!artifact.isResolved() && !destination.exists()) {
                            throw new ArtifactResolutionException("Failed to resolve artifact, possibly due to a repository list that is not appropriately equipped for this artifact's metadata.", artifact, this.getMirroredRepositories(remoteRepositories));
                        }
                        break Label_0659;
                    }
                    catch (ResourceDoesNotExistException e) {
                        throw new ArtifactNotFoundException(e.getMessage(), artifact, this.getMirroredRepositories(remoteRepositories), e);
                    }
                    catch (TransferFailedException e2) {
                        throw new ArtifactResolutionException(e2.getMessage(), artifact, this.getMirroredRepositories(remoteRepositories), e2);
                    }
                }
                if (destination.exists()) {
                    artifact.setResolved(true);
                }
            }
            if (artifact.isSnapshot() && !artifact.getBaseVersion().equals(artifact.getVersion())) {
                final String version = artifact.getVersion();
                artifact.selectVersion(artifact.getBaseVersion());
                final File copy = new File(localRepository.getBasedir(), localRepository.pathOf(artifact));
                Label_0827: {
                    if (copy.exists() && copy.lastModified() == destination.lastModified()) {
                        if (copy.length() == destination.length()) {
                            break Label_0827;
                        }
                    }
                    try {
                        FileUtils.copyFile(destination, copy);
                        copy.setLastModified(destination.lastModified());
                    }
                    catch (IOException e3) {
                        throw new ArtifactResolutionException("Unable to copy resolved artifact for local use: " + e3.getMessage(), artifact, this.getMirroredRepositories(remoteRepositories), e3);
                    }
                }
                artifact.setFile(copy);
                artifact.selectVersion(version);
            }
        }
    }
    
    public ArtifactResolutionResult resolveTransitively(final Set artifacts, final Artifact originatingArtifact, final ArtifactRepository localRepository, final List remoteRepositories, final ArtifactMetadataSource source, final ArtifactFilter filter) throws ArtifactResolutionException, ArtifactNotFoundException {
        return this.resolveTransitively(artifacts, originatingArtifact, Collections.EMPTY_MAP, localRepository, remoteRepositories, source, filter);
    }
    
    public ArtifactResolutionResult resolveTransitively(final Set artifacts, final Artifact originatingArtifact, final Map managedVersions, final ArtifactRepository localRepository, final List remoteRepositories, final ArtifactMetadataSource source) throws ArtifactResolutionException, ArtifactNotFoundException {
        return this.resolveTransitively(artifacts, originatingArtifact, managedVersions, localRepository, remoteRepositories, source, null);
    }
    
    public ArtifactResolutionResult resolveTransitively(final Set artifacts, final Artifact originatingArtifact, final Map managedVersions, final ArtifactRepository localRepository, final List remoteRepositories, final ArtifactMetadataSource source, final ArtifactFilter filter) throws ArtifactResolutionException, ArtifactNotFoundException {
        final List listeners = new ArrayList();
        if (this.getLogger().isDebugEnabled()) {
            listeners.add(new DebugResolutionListener(this.getLogger()));
        }
        listeners.add(new WarningResolutionListener(this.getLogger()));
        return this.resolveTransitively(artifacts, originatingArtifact, managedVersions, localRepository, remoteRepositories, source, filter, listeners);
    }
    
    public ArtifactResolutionResult resolveTransitively(final Set artifacts, final Artifact originatingArtifact, final Map managedVersions, final ArtifactRepository localRepository, final List remoteRepositories, final ArtifactMetadataSource source, final ArtifactFilter filter, final List listeners) throws ArtifactResolutionException, ArtifactNotFoundException {
        final ArtifactResolutionResult artifactResolutionResult = this.artifactCollector.collect(artifacts, originatingArtifact, managedVersions, localRepository, remoteRepositories, source, filter, listeners);
        final List resolvedArtifacts = Collections.synchronizedList(new ArrayList<Object>());
        final List missingArtifacts = Collections.synchronizedList(new ArrayList<Object>());
        final CountDownLatch latch = new CountDownLatch(artifactResolutionResult.getArtifactResolutionNodes().size());
        final Map nodesByGroupId = new HashMap();
        for (final ResolutionNode node : artifactResolutionResult.getArtifactResolutionNodes()) {
            List nodes = nodesByGroupId.get(node.getArtifact().getGroupId());
            if (nodes == null) {
                nodes = new ArrayList();
                nodesByGroupId.put(node.getArtifact().getGroupId(), nodes);
            }
            nodes.add(node);
        }
        final List resolutionExceptions = Collections.synchronizedList(new ArrayList<Object>());
        try {
            for (final List nodes : nodesByGroupId.values()) {
                this.resolveArtifactPool.execute(new ResolveArtifactTask(this.resolveArtifactPool, latch, nodes, localRepository, resolvedArtifacts, missingArtifacts, resolutionExceptions));
            }
            latch.await();
        }
        catch (InterruptedException e) {
            throw new ArtifactResolutionException("Resolution interrupted", originatingArtifact, e);
        }
        if (!resolutionExceptions.isEmpty()) {
            throw (ArtifactResolutionException)resolutionExceptions.get(0);
        }
        if (missingArtifacts.size() > 0) {
            throw new MultipleArtifactsNotFoundException(originatingArtifact, resolvedArtifacts, missingArtifacts, this.getMirroredRepositories(remoteRepositories));
        }
        return artifactResolutionResult;
    }
    
    private List getMirroredRepositories(final List remoteRepositories) {
        final Map repos = new HashMap();
        for (final ArtifactRepository repository : remoteRepositories) {
            final ArtifactRepository repo = this.wagonManager.getMirrorRepository(repository);
            repos.put(repo.getId(), repo);
        }
        return new ArrayList(repos.values());
    }
    
    public ArtifactResolutionResult resolveTransitively(final Set artifacts, final Artifact originatingArtifact, final List remoteRepositories, final ArtifactRepository localRepository, final ArtifactMetadataSource source) throws ArtifactResolutionException, ArtifactNotFoundException {
        return this.resolveTransitively(artifacts, originatingArtifact, localRepository, remoteRepositories, source, null);
    }
    
    public ArtifactResolutionResult resolveTransitively(final Set artifacts, final Artifact originatingArtifact, final List remoteRepositories, final ArtifactRepository localRepository, final ArtifactMetadataSource source, final List listeners) throws ArtifactResolutionException, ArtifactNotFoundException {
        return this.resolveTransitively(artifacts, originatingArtifact, Collections.EMPTY_MAP, localRepository, remoteRepositories, source, null, listeners);
    }
    
    public synchronized void configureNumberOfThreads(final int threads) {
        this.resolveArtifactPool.setCorePoolSize(threads);
        this.resolveArtifactPool.setMaximumPoolSize(threads);
    }
    
    void setWagonManager(final WagonManager wagonManager) {
        this.wagonManager = wagonManager;
    }
    
    private class ResolveArtifactTask implements Runnable
    {
        private List nodes;
        private ArtifactRepository localRepository;
        private List resolvedArtifacts;
        private List missingArtifacts;
        private CountDownLatch latch;
        private ThreadPoolExecutor pool;
        private List resolutionExceptions;
        
        public ResolveArtifactTask(final ThreadPoolExecutor pool, final CountDownLatch latch, final List nodes, final ArtifactRepository localRepository, final List resolvedArtifacts, final List missingArtifacts, final List resolutionExceptions) {
            this.nodes = nodes;
            this.localRepository = localRepository;
            this.resolvedArtifacts = resolvedArtifacts;
            this.missingArtifacts = missingArtifacts;
            this.latch = latch;
            this.pool = pool;
            this.resolutionExceptions = resolutionExceptions;
        }
        
        public void run() {
            final Iterator i = this.nodes.iterator();
            final ResolutionNode node = i.next();
            i.remove();
            try {
                this.resolveArtifact(node);
            }
            catch (ArtifactResolutionException e) {
                this.resolutionExceptions.add(e);
            }
            finally {
                this.latch.countDown();
                if (i.hasNext()) {
                    this.pool.execute(new ResolveArtifactTask(this.pool, this.latch, this.nodes, this.localRepository, this.resolvedArtifacts, this.missingArtifacts, this.resolutionExceptions));
                }
            }
        }
        
        private void resolveArtifact(final ResolutionNode node) throws ArtifactResolutionException {
            try {
                DefaultArtifactResolver.this.resolve(node.getArtifact(), node.getRemoteRepositories(), this.localRepository);
                this.resolvedArtifacts.add(node.getArtifact());
            }
            catch (ArtifactNotFoundException anfe) {
                AbstractLogEnabled.this.getLogger().debug(anfe.getMessage(), anfe);
                this.missingArtifacts.add(node.getArtifact());
            }
        }
    }
}
