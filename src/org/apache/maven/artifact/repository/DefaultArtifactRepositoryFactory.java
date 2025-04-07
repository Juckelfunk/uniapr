// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.repository;

import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import java.util.HashMap;
import java.util.Map;

public class DefaultArtifactRepositoryFactory implements ArtifactRepositoryFactory
{
    private String globalUpdatePolicy;
    private String globalChecksumPolicy;
    private final Map artifactRepositories;
    
    public DefaultArtifactRepositoryFactory() {
        this.artifactRepositories = new HashMap();
    }
    
    public ArtifactRepository createDeploymentArtifactRepository(final String id, final String url, final ArtifactRepositoryLayout repositoryLayout, final boolean uniqueVersion) {
        return new DefaultArtifactRepository(id, url, repositoryLayout, uniqueVersion);
    }
    
    public ArtifactRepository createArtifactRepository(final String id, final String url, final ArtifactRepositoryLayout repositoryLayout, ArtifactRepositoryPolicy snapshots, ArtifactRepositoryPolicy releases) {
        boolean blacklisted = false;
        if (this.artifactRepositories.containsKey(id)) {
            final ArtifactRepository repository = this.artifactRepositories.get(id);
            if (repository.getUrl().equals(url)) {
                blacklisted = repository.isBlacklisted();
            }
        }
        if (snapshots == null) {
            snapshots = new ArtifactRepositoryPolicy();
        }
        if (releases == null) {
            releases = new ArtifactRepositoryPolicy();
        }
        if (this.globalUpdatePolicy != null) {
            snapshots.setUpdatePolicy(this.globalUpdatePolicy);
            releases.setUpdatePolicy(this.globalUpdatePolicy);
        }
        if (this.globalChecksumPolicy != null) {
            snapshots.setChecksumPolicy(this.globalChecksumPolicy);
            releases.setChecksumPolicy(this.globalChecksumPolicy);
        }
        final DefaultArtifactRepository repository2 = new DefaultArtifactRepository(id, url, repositoryLayout, snapshots, releases);
        repository2.setBlacklisted(blacklisted);
        this.artifactRepositories.put(id, repository2);
        return repository2;
    }
    
    public void setGlobalUpdatePolicy(final String updatePolicy) {
        this.globalUpdatePolicy = updatePolicy;
    }
    
    public void setGlobalChecksumPolicy(final String checksumPolicy) {
        this.globalChecksumPolicy = checksumPolicy;
    }
}
