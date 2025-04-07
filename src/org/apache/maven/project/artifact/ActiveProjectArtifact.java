// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project.artifact;

import org.apache.maven.artifact.versioning.OverConstrainedVersionException;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;
import java.util.List;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.repository.ArtifactRepository;
import java.util.Collection;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import java.io.File;
import org.apache.maven.project.MavenProject;
import org.apache.maven.artifact.Artifact;

public class ActiveProjectArtifact implements Artifact
{
    private final Artifact artifact;
    private final MavenProject project;
    
    public ActiveProjectArtifact(final MavenProject project, final Artifact artifact) {
        this.artifact = artifact;
        this.project = project;
        artifact.setFile(project.getArtifact().getFile());
        artifact.setResolved(true);
    }
    
    public File getFile() {
        return this.project.getArtifact().getFile();
    }
    
    public String getGroupId() {
        return this.artifact.getGroupId();
    }
    
    public String getArtifactId() {
        return this.artifact.getArtifactId();
    }
    
    public String getVersion() {
        return this.artifact.getVersion();
    }
    
    public void setVersion(final String version) {
        this.artifact.setVersion(version);
    }
    
    public String getScope() {
        return this.artifact.getScope();
    }
    
    public String getType() {
        return this.artifact.getType();
    }
    
    public String getClassifier() {
        return this.artifact.getClassifier();
    }
    
    public boolean hasClassifier() {
        return this.artifact.hasClassifier();
    }
    
    public void setFile(final File destination) {
        this.artifact.setFile(destination);
        this.project.getArtifact().setFile(destination);
    }
    
    public String getBaseVersion() {
        return this.artifact.getBaseVersion();
    }
    
    public void setBaseVersion(final String baseVersion) {
        this.artifact.setBaseVersion(baseVersion);
    }
    
    public String getId() {
        return this.artifact.getId();
    }
    
    public String getDependencyConflictId() {
        return this.artifact.getDependencyConflictId();
    }
    
    public void addMetadata(final ArtifactMetadata metadata) {
        this.artifact.addMetadata(metadata);
    }
    
    public Collection<ArtifactMetadata> getMetadataList() {
        return this.artifact.getMetadataList();
    }
    
    public void setRepository(final ArtifactRepository remoteRepository) {
        this.artifact.setRepository(remoteRepository);
    }
    
    public ArtifactRepository getRepository() {
        return this.artifact.getRepository();
    }
    
    public void updateVersion(final String version, final ArtifactRepository localRepository) {
        this.artifact.updateVersion(version, localRepository);
    }
    
    public String getDownloadUrl() {
        return this.artifact.getDownloadUrl();
    }
    
    public void setDownloadUrl(final String downloadUrl) {
        this.artifact.setDownloadUrl(downloadUrl);
    }
    
    public ArtifactFilter getDependencyFilter() {
        return this.artifact.getDependencyFilter();
    }
    
    public void setDependencyFilter(final ArtifactFilter artifactFilter) {
        this.artifact.setDependencyFilter(artifactFilter);
    }
    
    public ArtifactHandler getArtifactHandler() {
        return this.artifact.getArtifactHandler();
    }
    
    public List<String> getDependencyTrail() {
        return this.artifact.getDependencyTrail();
    }
    
    public void setDependencyTrail(final List<String> dependencyTrail) {
        this.artifact.setDependencyTrail(dependencyTrail);
    }
    
    public void setScope(final String scope) {
        this.artifact.setScope(scope);
    }
    
    public VersionRange getVersionRange() {
        return this.artifact.getVersionRange();
    }
    
    public void setVersionRange(final VersionRange newRange) {
        this.artifact.setVersionRange(newRange);
    }
    
    public void selectVersion(final String version) {
        this.artifact.selectVersion(version);
    }
    
    public void setGroupId(final String groupId) {
        this.artifact.setGroupId(groupId);
    }
    
    public void setArtifactId(final String artifactId) {
        this.artifact.setArtifactId(artifactId);
    }
    
    public boolean isSnapshot() {
        return this.artifact.isSnapshot();
    }
    
    public int compareTo(final Artifact a) {
        return this.artifact.compareTo(a);
    }
    
    public void setResolved(final boolean resolved) {
        this.artifact.setResolved(resolved);
    }
    
    public boolean isResolved() {
        return this.artifact.isResolved();
    }
    
    public void setResolvedVersion(final String version) {
        this.artifact.setResolvedVersion(version);
    }
    
    public void setArtifactHandler(final ArtifactHandler handler) {
        this.artifact.setArtifactHandler(handler);
    }
    
    @Override
    public String toString() {
        return "active project artifact:\n\tartifact = " + this.artifact + ";\n\tproject: " + this.project;
    }
    
    public boolean isRelease() {
        return this.artifact.isRelease();
    }
    
    public void setRelease(final boolean release) {
        this.artifact.setRelease(release);
    }
    
    public List<ArtifactVersion> getAvailableVersions() {
        return this.artifact.getAvailableVersions();
    }
    
    public void setAvailableVersions(final List<ArtifactVersion> versions) {
        this.artifact.setAvailableVersions(versions);
    }
    
    public boolean isOptional() {
        return this.artifact.isOptional();
    }
    
    public ArtifactVersion getSelectedVersion() throws OverConstrainedVersionException {
        return this.artifact.getSelectedVersion();
    }
    
    public boolean isSelectedVersionKnown() throws OverConstrainedVersionException {
        return this.artifact.isSelectedVersionKnown();
    }
    
    public void setOptional(final boolean optional) {
        this.artifact.setOptional(optional);
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + this.getGroupId().hashCode();
        result = 37 * result + this.getArtifactId().hashCode();
        result = 37 * result + this.getType().hashCode();
        if (this.getVersion() != null) {
            result = 37 * result + this.getVersion().hashCode();
        }
        result = 37 * result + ((this.getClassifier() != null) ? this.getClassifier().hashCode() : 0);
        return result;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Artifact)) {
            return false;
        }
        final Artifact a = (Artifact)o;
        if (!a.getGroupId().equals(this.getGroupId())) {
            return false;
        }
        if (!a.getArtifactId().equals(this.getArtifactId())) {
            return false;
        }
        if (!a.getVersion().equals(this.getVersion())) {
            return false;
        }
        if (!a.getType().equals(this.getType())) {
            return false;
        }
        if (a.getClassifier() == null) {
            if (this.getClassifier() == null) {
                return true;
            }
        }
        else if (a.getClassifier().equals(this.getClassifier())) {
            return true;
        }
        return false;
    }
    
    public ArtifactMetadata getMetadata(final Class<?> metadataClass) {
        return this.artifact.getMetadata(metadataClass);
    }
}
