// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.apache.maven.model.RepositoryPolicy;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.model.RepositoryBase;
import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.artifact.InvalidRepositoryException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import java.util.Iterator;
import org.apache.maven.model.Repository;
import java.util.ArrayList;
import org.codehaus.plexus.PlexusContainer;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import java.util.List;

public final class ProjectUtils
{
    private ProjectUtils() {
    }
    
    public static List buildArtifactRepositories(final List repositories, final ArtifactRepositoryFactory artifactRepositoryFactory, final PlexusContainer container) throws InvalidRepositoryException {
        final List repos = new ArrayList();
        for (final Repository mavenRepo : repositories) {
            final ArtifactRepository artifactRepo = buildArtifactRepository(mavenRepo, artifactRepositoryFactory, container);
            if (!repos.contains(artifactRepo)) {
                repos.add(artifactRepo);
            }
        }
        return repos;
    }
    
    public static ArtifactRepository buildDeploymentArtifactRepository(final DeploymentRepository repo, final ArtifactRepositoryFactory artifactRepositoryFactory, final PlexusContainer container) throws InvalidRepositoryException {
        if (repo != null) {
            final String id = repo.getId();
            final String url = repo.getUrl();
            final ArtifactRepositoryLayout layout = getRepositoryLayout(repo, container);
            return artifactRepositoryFactory.createDeploymentArtifactRepository(id, url, layout, repo.isUniqueVersion());
        }
        return null;
    }
    
    public static ArtifactRepository buildArtifactRepository(final Repository repo, final ArtifactRepositoryFactory artifactRepositoryFactory, final PlexusContainer container) throws InvalidRepositoryException {
        if (repo == null) {
            return null;
        }
        final String id = repo.getId();
        final String url = repo.getUrl();
        if (id == null || id.trim().length() < 1) {
            throw new InvalidRepositoryException("Repository ID must not be empty (URL is: " + url + ").", new IllegalArgumentException("repository.id"));
        }
        if (url == null || url.trim().length() < 1) {
            throw new InvalidRepositoryException("Repository URL must not be empty (ID is: " + id + ").", new IllegalArgumentException("repository.url"));
        }
        final ArtifactRepositoryLayout layout = getRepositoryLayout(repo, container);
        final ArtifactRepositoryPolicy snapshots = buildArtifactRepositoryPolicy(repo.getSnapshots());
        final ArtifactRepositoryPolicy releases = buildArtifactRepositoryPolicy(repo.getReleases());
        return artifactRepositoryFactory.createArtifactRepository(id, url, layout, snapshots, releases);
    }
    
    private static ArtifactRepositoryPolicy buildArtifactRepositoryPolicy(final RepositoryPolicy policy) {
        boolean enabled = true;
        String updatePolicy = null;
        String checksumPolicy = null;
        if (policy != null) {
            enabled = policy.isEnabled();
            if (policy.getUpdatePolicy() != null) {
                updatePolicy = policy.getUpdatePolicy();
            }
            if (policy.getChecksumPolicy() != null) {
                checksumPolicy = policy.getChecksumPolicy();
            }
        }
        return new ArtifactRepositoryPolicy(enabled, updatePolicy, checksumPolicy);
    }
    
    private static ArtifactRepositoryLayout getRepositoryLayout(final RepositoryBase mavenRepo, final PlexusContainer container) throws InvalidRepositoryException {
        final String layout = mavenRepo.getLayout();
        ArtifactRepositoryLayout repositoryLayout;
        try {
            repositoryLayout = (ArtifactRepositoryLayout)container.lookup(ArtifactRepositoryLayout.ROLE, layout);
        }
        catch (ComponentLookupException e) {
            throw new InvalidRepositoryException("Cannot find layout implementation corresponding to: '" + layout + "' for remote repository with id: '" + mavenRepo.getId() + "'.", e);
        }
        return repositoryLayout;
    }
}
