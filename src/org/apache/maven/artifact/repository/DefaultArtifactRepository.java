// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.repository;

import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.wagon.repository.Repository;

public class DefaultArtifactRepository extends Repository implements ArtifactRepository
{
    private final ArtifactRepositoryLayout layout;
    private ArtifactRepositoryPolicy snapshots;
    private ArtifactRepositoryPolicy releases;
    private boolean uniqueVersion;
    private boolean blacklisted;
    
    public DefaultArtifactRepository(final String id, final String url, final ArtifactRepositoryLayout layout) {
        this(id, url, layout, null, null);
    }
    
    public DefaultArtifactRepository(final String id, final String url, final ArtifactRepositoryLayout layout, final boolean uniqueVersion) {
        super(id, url);
        this.layout = layout;
        this.uniqueVersion = uniqueVersion;
    }
    
    public DefaultArtifactRepository(final String id, final String url, final ArtifactRepositoryLayout layout, ArtifactRepositoryPolicy snapshots, ArtifactRepositoryPolicy releases) {
        super(id, url);
        this.layout = layout;
        if (snapshots == null) {
            snapshots = new ArtifactRepositoryPolicy(true, "always", "ignore");
        }
        this.snapshots = snapshots;
        if (releases == null) {
            releases = new ArtifactRepositoryPolicy(true, "always", "ignore");
        }
        this.releases = releases;
    }
    
    public String pathOf(final Artifact artifact) {
        return this.layout.pathOf(artifact);
    }
    
    public String pathOfRemoteRepositoryMetadata(final ArtifactMetadata artifactMetadata) {
        return this.layout.pathOfRemoteRepositoryMetadata(artifactMetadata);
    }
    
    public String pathOfLocalRepositoryMetadata(final ArtifactMetadata metadata, final ArtifactRepository repository) {
        return this.layout.pathOfLocalRepositoryMetadata(metadata, repository);
    }
    
    public ArtifactRepositoryLayout getLayout() {
        return this.layout;
    }
    
    public ArtifactRepositoryPolicy getSnapshots() {
        return this.snapshots;
    }
    
    public ArtifactRepositoryPolicy getReleases() {
        return this.releases;
    }
    
    public String getKey() {
        return this.getId();
    }
    
    public boolean isUniqueVersion() {
        return this.uniqueVersion;
    }
    
    public boolean isBlacklisted() {
        return this.blacklisted;
    }
    
    public void setBlacklisted(final boolean blacklisted) {
        this.blacklisted = blacklisted;
    }
}
