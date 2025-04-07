// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project.artifact;

import java.util.Collection;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.artifact.repository.ArtifactRepository;
import java.util.List;
import org.apache.maven.artifact.InvalidArtifactRTException;
import java.util.Collections;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;

public class AttachedArtifact extends DefaultArtifact
{
    private final Artifact parent;
    
    public AttachedArtifact(final Artifact parent, final String type, final String classifier, final ArtifactHandler artifactHandler) {
        super(parent.getGroupId(), parent.getArtifactId(), parent.getVersionRange(), parent.getScope(), type, classifier, artifactHandler, parent.isOptional());
        this.setDependencyTrail(Collections.singletonList(parent.getId()));
        this.parent = parent;
        if (this.getId().equals(parent.getId())) {
            throw new InvalidArtifactRTException(parent.getGroupId(), parent.getArtifactId(), parent.getVersion(), parent.getType(), "An attached artifact must have a different ID than its corresponding main artifact.");
        }
    }
    
    public AttachedArtifact(final Artifact parent, final String type, final ArtifactHandler artifactHandler) {
        this(parent, type, null, artifactHandler);
    }
    
    @Override
    public void setArtifactId(final String artifactId) {
        throw new UnsupportedOperationException("Cannot change the artifactId for an attached artifact.  It is derived from the main artifact.");
    }
    
    @Override
    public List getAvailableVersions() {
        return this.parent.getAvailableVersions();
    }
    
    @Override
    public void setAvailableVersions(final List availableVersions) {
        throw new UnsupportedOperationException("Cannot change the version information for an attached artifact. It is derived from the main artifact.");
    }
    
    @Override
    public String getBaseVersion() {
        return this.parent.getBaseVersion();
    }
    
    @Override
    public void setBaseVersion(final String baseVersion) {
        throw new UnsupportedOperationException("Cannot change the version information for an attached artifact. It is derived from the main artifact.");
    }
    
    @Override
    public String getDownloadUrl() {
        return this.parent.getDownloadUrl();
    }
    
    @Override
    public void setDownloadUrl(final String downloadUrl) {
        throw new UnsupportedOperationException("Cannot change the download information for an attached artifact. It is derived from the main artifact.");
    }
    
    @Override
    public void setGroupId(final String groupId) {
        throw new UnsupportedOperationException("Cannot change the groupId on attached artifacts. It is derived from the main artifact.");
    }
    
    @Override
    public ArtifactRepository getRepository() {
        return this.parent.getRepository();
    }
    
    @Override
    public void setRepository(final ArtifactRepository repository) {
        throw new UnsupportedOperationException("Cannot change the repository information for an attached artifact. It is derived from the main artifact.");
    }
    
    @Override
    public String getScope() {
        return this.parent.getScope();
    }
    
    @Override
    public void setScope(final String scope) {
        throw new UnsupportedOperationException("Cannot change the scoping information for an attached artifact. It is derived from the main artifact.");
    }
    
    @Override
    public String getVersion() {
        return this.parent.getVersion();
    }
    
    @Override
    public void setVersion(final String version) {
        throw new UnsupportedOperationException("Cannot change the version information for an attached artifact. It is derived from the main artifact.");
    }
    
    @Override
    public VersionRange getVersionRange() {
        return this.parent.getVersionRange();
    }
    
    @Override
    public void setVersionRange(final VersionRange range) {
        throw new UnsupportedOperationException("Cannot change the version information for an attached artifact. It is derived from the main artifact.");
    }
    
    @Override
    public boolean isRelease() {
        return this.parent.isRelease();
    }
    
    @Override
    public void setRelease(final boolean release) {
        throw new UnsupportedOperationException("Cannot change the version information for an attached artifact. It is derived from the main artifact.");
    }
    
    @Override
    public boolean isSnapshot() {
        return this.parent.isSnapshot();
    }
    
    @Override
    public void addMetadata(final ArtifactMetadata metadata) {
    }
    
    @Override
    public Collection getMetadataList() {
        return Collections.EMPTY_LIST;
    }
}
