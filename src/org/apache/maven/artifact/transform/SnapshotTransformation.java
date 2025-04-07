// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.transform;

import java.text.SimpleDateFormat;
import java.text.DateFormat;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.codehaus.plexus.util.StringUtils;
import org.apache.maven.artifact.repository.metadata.Versioning;
import java.util.Date;
import org.apache.maven.artifact.deployer.ArtifactDeploymentException;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadata;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.metadata.SnapshotArtifactRepositoryMetadata;
import org.apache.maven.artifact.repository.metadata.Snapshot;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import java.util.TimeZone;

public class SnapshotTransformation extends AbstractVersionTransformation
{
    private String deploymentTimestamp;
    private static final TimeZone UTC_TIME_ZONE;
    private static final String UTC_TIMESTAMP_PATTERN = "yyyyMMdd.HHmmss";
    
    public void transformForResolve(final Artifact artifact, final List<ArtifactRepository> remoteRepositories, final ArtifactRepository localRepository) throws ArtifactResolutionException {
        if (artifact.isSnapshot() && artifact.getBaseVersion().equals(artifact.getVersion())) {
            try {
                final String version = this.resolveVersion(artifact, localRepository, remoteRepositories);
                artifact.updateVersion(version, localRepository);
            }
            catch (RepositoryMetadataResolutionException e) {
                throw new ArtifactResolutionException(e.getMessage(), artifact, e);
            }
        }
    }
    
    public void transformForInstall(final Artifact artifact, final ArtifactRepository localRepository) {
        if (artifact.isSnapshot()) {
            final Snapshot snapshot = new Snapshot();
            snapshot.setLocalCopy(true);
            final RepositoryMetadata metadata = new SnapshotArtifactRepositoryMetadata(artifact, snapshot);
            artifact.addMetadata(metadata);
        }
    }
    
    public void transformForDeployment(final Artifact artifact, final ArtifactRepository remoteRepository, final ArtifactRepository localRepository) throws ArtifactDeploymentException {
        if (artifact.isSnapshot()) {
            final Snapshot snapshot = new Snapshot();
            if (remoteRepository.isUniqueVersion()) {
                snapshot.setTimestamp(this.getDeploymentTimestamp());
            }
            try {
                final int buildNumber = this.resolveLatestSnapshotBuildNumber(artifact, localRepository, remoteRepository);
                snapshot.setBuildNumber(buildNumber + 1);
            }
            catch (RepositoryMetadataResolutionException e) {
                throw new ArtifactDeploymentException("Error retrieving previous build number for artifact '" + artifact.getDependencyConflictId() + "': " + e.getMessage(), e);
            }
            final RepositoryMetadata metadata = new SnapshotArtifactRepositoryMetadata(artifact, snapshot);
            artifact.setResolvedVersion(this.constructVersion(metadata.getMetadata().getVersioning(), artifact.getBaseVersion()));
            artifact.addMetadata(metadata);
        }
    }
    
    public String getDeploymentTimestamp() {
        if (this.deploymentTimestamp == null) {
            this.deploymentTimestamp = getUtcDateFormatter().format(new Date());
        }
        return this.deploymentTimestamp;
    }
    
    @Override
    protected String constructVersion(final Versioning versioning, final String baseVersion) {
        String version = null;
        final Snapshot snapshot = versioning.getSnapshot();
        if (snapshot != null) {
            if (snapshot.getTimestamp() != null && snapshot.getBuildNumber() > 0) {
                final String newVersion = snapshot.getTimestamp() + "-" + snapshot.getBuildNumber();
                version = StringUtils.replace(baseVersion, "SNAPSHOT", newVersion);
            }
            else {
                version = baseVersion;
            }
        }
        return version;
    }
    
    private int resolveLatestSnapshotBuildNumber(final Artifact artifact, final ArtifactRepository localRepository, final ArtifactRepository remoteRepository) throws RepositoryMetadataResolutionException {
        final RepositoryMetadata metadata = new SnapshotArtifactRepositoryMetadata(artifact);
        if (!this.wagonManager.isOnline()) {
            throw new RepositoryMetadataResolutionException("System is offline. Cannot resolve metadata:\n" + metadata.extendedToString() + "\n\n");
        }
        this.getLogger().info("Retrieving previous build number from " + remoteRepository.getId());
        this.repositoryMetadataManager.resolveAlways(metadata, localRepository, remoteRepository);
        int buildNumber = 0;
        final Metadata repoMetadata = metadata.getMetadata();
        if (repoMetadata != null && repoMetadata.getVersioning() != null && repoMetadata.getVersioning().getSnapshot() != null) {
            buildNumber = repoMetadata.getVersioning().getSnapshot().getBuildNumber();
        }
        return buildNumber;
    }
    
    public static DateFormat getUtcDateFormatter() {
        final DateFormat utcDateFormatter = new SimpleDateFormat("yyyyMMdd.HHmmss");
        utcDateFormatter.setTimeZone(SnapshotTransformation.UTC_TIME_ZONE);
        return utcDateFormatter;
    }
    
    static {
        UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");
    }
}
