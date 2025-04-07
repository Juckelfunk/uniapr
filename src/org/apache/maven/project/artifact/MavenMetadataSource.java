// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project.artifact;

import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadata;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataResolutionException;
import org.apache.maven.artifact.repository.metadata.ArtifactRepositoryMetadata;
import org.apache.maven.artifact.resolver.filter.AndArtifactFilter;
import org.apache.maven.artifact.resolver.filter.ExcludesArtifactFilter;
import org.apache.maven.model.Exclusion;
import java.io.File;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.codehaus.plexus.util.StringUtils;
import org.apache.maven.model.Dependency;
import java.util.LinkedHashSet;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.maven.project.ProjectBuilderConfiguration;
import org.apache.maven.project.DefaultProjectBuilderConfiguration;
import java.util.Collections;
import org.apache.maven.artifact.metadata.ResolutionGroup;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Relocation;
import java.util.Iterator;
import org.apache.maven.project.validation.ModelValidationResult;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.InvalidProjectModelException;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import java.util.List;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.Artifact;
import java.util.HashSet;
import java.util.Set;
import org.apache.maven.project.MavenProject;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataManager;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.codehaus.plexus.logging.AbstractLogEnabled;

public class MavenMetadataSource extends AbstractLogEnabled implements ArtifactMetadataSource
{
    public static final String ROLE_HINT = "maven";
    private MavenProjectBuilder mavenProjectBuilder;
    private ArtifactFactory artifactFactory;
    private RepositoryMetadataManager repositoryMetadataManager;
    private MavenProject superProject;
    private Set warnedPoms;
    
    public MavenMetadataSource() {
        this.warnedPoms = new HashSet();
    }
    
    public Artifact retrieveRelocatedArtifact(final Artifact artifact, final ArtifactRepository localRepository, final List remoteRepositories) throws ArtifactMetadataRetrievalException {
        if (artifact instanceof ActiveProjectArtifact) {
            return artifact;
        }
        final ProjectRelocation rel = this.retrieveRelocatedProject(artifact, localRepository, remoteRepositories);
        if (rel == null) {
            return artifact;
        }
        final MavenProject project = rel.project;
        if (project == null || this.getRelocationKey(artifact).equals(this.getRelocationKey(project.getArtifact()))) {
            return artifact;
        }
        Artifact result = null;
        if (artifact.getClassifier() != null) {
            result = this.artifactFactory.createArtifactWithClassifier(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), artifact.getType(), artifact.getClassifier());
        }
        else {
            result = this.artifactFactory.createArtifact(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), artifact.getScope(), artifact.getType());
        }
        result.setResolved(artifact.isResolved());
        result.setFile(artifact.getFile());
        result.setScope(artifact.getScope());
        result.setArtifactHandler(artifact.getArtifactHandler());
        result.setDependencyFilter(artifact.getDependencyFilter());
        result.setDependencyTrail(artifact.getDependencyTrail());
        result.setOptional(artifact.isOptional());
        result.setRelease(artifact.isRelease());
        return result;
    }
    
    private String getRelocationKey(final Artifact artifact) {
        return artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getVersion();
    }
    
    private ProjectRelocation retrieveRelocatedProject(final Artifact artifact, final ArtifactRepository localRepository, final List remoteRepositories) throws ArtifactMetadataRetrievalException {
        MavenProject project = null;
        boolean done = false;
        Artifact pomArtifact;
        do {
            pomArtifact = this.artifactFactory.createProjectArtifact(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), artifact.getScope());
            if ("system".equals(artifact.getScope())) {
                done = true;
            }
            else {
                try {
                    project = this.mavenProjectBuilder.buildFromRepository(pomArtifact, remoteRepositories, localRepository, true);
                }
                catch (InvalidProjectModelException e) {
                    final String id = pomArtifact.getId();
                    if (!this.warnedPoms.contains(id)) {
                        this.warnedPoms.add(pomArtifact.getId());
                        this.getLogger().warn("POM for '" + pomArtifact + "' is invalid.\n\nIts dependencies (if any) will NOT be available to the current build.");
                        if (this.getLogger().isDebugEnabled()) {
                            this.getLogger().debug("Reason: " + e.getMessage());
                            final ModelValidationResult validationResult = e.getValidationResult();
                            if (validationResult != null) {
                                this.getLogger().debug("\nValidation Errors:");
                                final Iterator i = validationResult.getMessages().iterator();
                                while (i.hasNext()) {
                                    this.getLogger().debug(i.next().toString());
                                }
                                this.getLogger().debug("\n");
                            }
                        }
                    }
                    project = null;
                }
                catch (ProjectBuildingException e2) {
                    throw new ArtifactMetadataRetrievalException("Unable to read the metadata file for artifact '" + artifact.getDependencyConflictId() + "': " + e2.getMessage(), e2, artifact);
                }
                if (project != null) {
                    Relocation relocation = null;
                    final DistributionManagement distMgmt = project.getDistributionManagement();
                    if (distMgmt != null) {
                        relocation = distMgmt.getRelocation();
                        artifact.setDownloadUrl(distMgmt.getDownloadUrl());
                        pomArtifact.setDownloadUrl(distMgmt.getDownloadUrl());
                    }
                    if (relocation != null) {
                        if (relocation.getGroupId() != null) {
                            artifact.setGroupId(relocation.getGroupId());
                            project.setGroupId(relocation.getGroupId());
                        }
                        if (relocation.getArtifactId() != null) {
                            artifact.setArtifactId(relocation.getArtifactId());
                            project.setArtifactId(relocation.getArtifactId());
                        }
                        if (relocation.getVersion() != null) {
                            artifact.setVersionRange(VersionRange.createFromVersion(relocation.getVersion()));
                            project.setVersion(relocation.getVersion());
                        }
                        if (artifact.getDependencyFilter() != null && !artifact.getDependencyFilter().include(artifact)) {
                            return null;
                        }
                        final List available = artifact.getAvailableVersions();
                        if (available != null && !available.isEmpty()) {
                            artifact.setAvailableVersions(this.retrieveAvailableVersions(artifact, localRepository, remoteRepositories));
                        }
                        String message = "\n  This artifact has been relocated to " + artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getVersion() + ".\n";
                        if (relocation.getMessage() != null) {
                            message = message + "  " + relocation.getMessage() + "\n";
                        }
                        if (artifact.getDependencyTrail() != null && artifact.getDependencyTrail().size() == 1) {
                            this.getLogger().warn("While downloading " + pomArtifact.getGroupId() + ":" + pomArtifact.getArtifactId() + ":" + pomArtifact.getVersion() + message + "\n");
                        }
                        else {
                            this.getLogger().debug("While downloading " + pomArtifact.getGroupId() + ":" + pomArtifact.getArtifactId() + ":" + pomArtifact.getVersion() + message + "\n");
                        }
                    }
                    else {
                        done = true;
                    }
                }
                else {
                    done = true;
                }
            }
        } while (!done);
        final ProjectRelocation rel = new ProjectRelocation();
        rel.project = project;
        rel.pomArtifact = pomArtifact;
        return rel;
    }
    
    public ResolutionGroup retrieve(final Artifact artifact, final ArtifactRepository localRepository, final List remoteRepositories) throws ArtifactMetadataRetrievalException {
        final ProjectRelocation rel = this.retrieveRelocatedProject(artifact, localRepository, remoteRepositories);
        if (rel == null) {
            return null;
        }
        final MavenProject project = rel.project;
        final Artifact pomArtifact = rel.pomArtifact;
        if (artifact.getDownloadUrl() == null && pomArtifact != null) {
            artifact.setDownloadUrl(pomArtifact.getDownloadUrl());
        }
        ResolutionGroup result;
        if (project == null) {
            result = new ResolutionGroup(pomArtifact, Collections.EMPTY_SET, Collections.EMPTY_LIST);
        }
        else {
            Set artifacts = Collections.EMPTY_SET;
            if (!artifact.getArtifactHandler().isIncludesDependencies()) {
                try {
                    artifacts = project.createArtifacts(this.artifactFactory, artifact.getScope(), artifact.getDependencyFilter());
                }
                catch (InvalidDependencyVersionException e) {
                    throw new ArtifactMetadataRetrievalException("Error in metadata for artifact '" + artifact.getDependencyConflictId() + "': " + e.getMessage(), e);
                }
            }
            final List repositories = this.aggregateRepositoryLists(remoteRepositories, project.getRemoteArtifactRepositories());
            result = new ResolutionGroup(pomArtifact, artifacts, repositories);
        }
        return result;
    }
    
    private List aggregateRepositoryLists(final List remoteRepositories, final List remoteArtifactRepositories) throws ArtifactMetadataRetrievalException {
        if (this.superProject == null) {
            try {
                this.superProject = this.mavenProjectBuilder.buildStandaloneSuperProject(new DefaultProjectBuilderConfiguration());
            }
            catch (ProjectBuildingException e) {
                throw new ArtifactMetadataRetrievalException("Unable to parse the Maven built-in model: " + e.getMessage(), e);
            }
        }
        final List repositories = new ArrayList();
        repositories.addAll(remoteRepositories);
        for (final ArtifactRepository superRepo : this.superProject.getRemoteArtifactRepositories()) {
            final Iterator aggregatedIterator = repositories.iterator();
            while (aggregatedIterator.hasNext()) {
                final ArtifactRepository repo = aggregatedIterator.next();
                if (repo.getId().equals(superRepo.getId()) && repo.getUrl().equals(superRepo.getUrl())) {
                    aggregatedIterator.remove();
                }
            }
        }
        for (final ArtifactRepository repository : remoteArtifactRepositories) {
            if (!repositories.contains(repository)) {
                repositories.add(repository);
            }
        }
        return repositories;
    }
    
    public static Set createArtifacts(final ArtifactFactory artifactFactory, final List dependencies, final String inheritedScope, final ArtifactFilter dependencyFilter, final MavenProject project) throws InvalidDependencyVersionException {
        final Set projectArtifacts = new LinkedHashSet(dependencies.size());
        for (final Dependency d : dependencies) {
            String scope = d.getScope();
            if (StringUtils.isEmpty(scope)) {
                scope = "compile";
                d.setScope(scope);
            }
            VersionRange versionRange;
            try {
                versionRange = VersionRange.createFromVersionSpec(d.getVersion());
            }
            catch (InvalidVersionSpecificationException e) {
                throw new InvalidDependencyVersionException("Unable to parse version '" + d.getVersion() + "' for dependency '" + d.getManagementKey() + "': " + e.getMessage(), e);
            }
            Artifact artifact = artifactFactory.createDependencyArtifact(d.getGroupId(), d.getArtifactId(), versionRange, d.getType(), d.getClassifier(), scope, inheritedScope, d.isOptional());
            if ("system".equals(scope)) {
                artifact.setFile(new File(d.getSystemPath()));
            }
            ArtifactFilter artifactFilter = dependencyFilter;
            if (artifact != null && (artifactFilter == null || artifactFilter.include(artifact))) {
                if (d.getExclusions() != null && !d.getExclusions().isEmpty()) {
                    final List exclusions = new ArrayList();
                    for (final Exclusion e2 : d.getExclusions()) {
                        exclusions.add(e2.getGroupId() + ":" + e2.getArtifactId());
                    }
                    final ArtifactFilter newFilter = new ExcludesArtifactFilter(exclusions);
                    if (artifactFilter != null) {
                        final AndArtifactFilter filter = new AndArtifactFilter();
                        filter.add(artifactFilter);
                        filter.add(newFilter);
                        artifactFilter = filter;
                    }
                    else {
                        artifactFilter = newFilter;
                    }
                }
                artifact.setDependencyFilter(artifactFilter);
                if (project != null) {
                    artifact = project.replaceWithActiveArtifact(artifact);
                }
                projectArtifacts.add(artifact);
            }
        }
        return projectArtifacts;
    }
    
    public List retrieveAvailableVersions(final Artifact artifact, final ArtifactRepository localRepository, final List remoteRepositories) throws ArtifactMetadataRetrievalException {
        final RepositoryMetadata metadata = new ArtifactRepositoryMetadata(artifact);
        try {
            this.repositoryMetadataManager.resolve(metadata, remoteRepositories, localRepository);
        }
        catch (RepositoryMetadataResolutionException e) {
            throw new ArtifactMetadataRetrievalException(e.getMessage(), e);
        }
        final Metadata repoMetadata = metadata.getMetadata();
        List versions;
        if (repoMetadata != null && repoMetadata.getVersioning() != null) {
            final List metadataVersions = repoMetadata.getVersioning().getVersions();
            versions = new ArrayList(metadataVersions.size());
            for (final String version : metadataVersions) {
                versions.add(new DefaultArtifactVersion(version));
            }
        }
        else {
            versions = Collections.EMPTY_LIST;
        }
        return versions;
    }
    
    private static final class ProjectRelocation
    {
        private MavenProject project;
        private Artifact pomArtifact;
    }
}
