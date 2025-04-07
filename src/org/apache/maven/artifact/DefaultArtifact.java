// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact;

import org.apache.maven.artifact.versioning.OverConstrainedVersionException;
import java.util.regex.Matcher;
import java.util.Collections;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashMap;
import org.codehaus.plexus.util.StringUtils;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import java.util.Map;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;
import java.util.List;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.repository.ArtifactRepository;
import java.io.File;

public class DefaultArtifact implements Artifact
{
    private String groupId;
    private String artifactId;
    private String baseVersion;
    private final String type;
    private final String classifier;
    private String scope;
    private File file;
    private ArtifactRepository repository;
    private String downloadUrl;
    private ArtifactFilter dependencyFilter;
    private ArtifactHandler artifactHandler;
    private List<String> dependencyTrail;
    private String version;
    private VersionRange versionRange;
    private boolean resolved;
    private boolean release;
    private List<ArtifactVersion> availableVersions;
    private Map<Object, ArtifactMetadata> metadataMap;
    private boolean optional;
    
    public DefaultArtifact(final String groupId, final String artifactId, final VersionRange versionRange, final String scope, final String type, final String classifier, final ArtifactHandler artifactHandler) {
        this(groupId, artifactId, versionRange, scope, type, classifier, artifactHandler, false);
    }
    
    public DefaultArtifact(final String groupId, final String artifactId, final VersionRange versionRange, final String scope, final String type, String classifier, final ArtifactHandler artifactHandler, final boolean optional) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.versionRange = versionRange;
        this.selectVersionFromNewRangeIfAvailable();
        this.artifactHandler = artifactHandler;
        this.scope = scope;
        this.type = type;
        if (classifier == null) {
            classifier = artifactHandler.getClassifier();
        }
        this.classifier = classifier;
        this.optional = optional;
        this.validateIdentity();
    }
    
    private void validateIdentity() {
        if (this.empty(this.groupId)) {
            throw new InvalidArtifactRTException(this.groupId, this.artifactId, this.getVersion(), this.type, "The groupId cannot be empty.");
        }
        if (this.artifactId == null) {
            throw new InvalidArtifactRTException(this.groupId, this.artifactId, this.getVersion(), this.type, "The artifactId cannot be empty.");
        }
        if (this.type == null) {
            throw new InvalidArtifactRTException(this.groupId, this.artifactId, this.getVersion(), this.type, "The type cannot be empty.");
        }
        if (this.version == null && this.versionRange == null) {
            throw new InvalidArtifactRTException(this.groupId, this.artifactId, this.getVersion(), this.type, "The version cannot be empty.");
        }
    }
    
    private boolean empty(final String value) {
        return value == null || value.trim().length() < 1;
    }
    
    public String getClassifier() {
        return this.classifier;
    }
    
    public boolean hasClassifier() {
        return StringUtils.isNotEmpty(this.classifier);
    }
    
    public String getScope() {
        return this.scope;
    }
    
    public String getGroupId() {
        return this.groupId;
    }
    
    public String getArtifactId() {
        return this.artifactId;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public void setVersion(final String version) {
        this.setBaseVersionInternal(this.version = version);
        this.versionRange = null;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setFile(final File file) {
        this.file = file;
    }
    
    public File getFile() {
        return this.file;
    }
    
    public ArtifactRepository getRepository() {
        return this.repository;
    }
    
    public void setRepository(final ArtifactRepository repository) {
        this.repository = repository;
    }
    
    public String getId() {
        return this.getDependencyConflictId() + ":" + this.getBaseVersion();
    }
    
    public String getDependencyConflictId() {
        final StringBuffer sb = new StringBuffer();
        sb.append(this.getGroupId());
        sb.append(":");
        this.appendArtifactTypeClassifierString(sb);
        return sb.toString();
    }
    
    private void appendArtifactTypeClassifierString(final StringBuffer sb) {
        sb.append(this.getArtifactId());
        sb.append(":");
        sb.append(this.getType());
        if (this.hasClassifier()) {
            sb.append(":");
            sb.append(this.getClassifier());
        }
    }
    
    public void addMetadata(final ArtifactMetadata metadata) {
        if (this.metadataMap == null) {
            this.metadataMap = new HashMap<Object, ArtifactMetadata>();
        }
        final ArtifactMetadata m = this.metadataMap.get(metadata.getKey());
        if (m != null) {
            m.merge(metadata);
        }
        else {
            this.metadataMap.put(metadata.getKey(), metadata);
        }
    }
    
    public ArtifactMetadata getMetadata(final Class<?> metadataClass) {
        final Collection<ArtifactMetadata> metadata = this.getMetadataList();
        if (metadata != null) {
            for (final ArtifactMetadata m : metadata) {
                if (metadataClass.isAssignableFrom(m.getClass())) {
                    return m;
                }
            }
        }
        return null;
    }
    
    public Collection<ArtifactMetadata> getMetadataList() {
        Collection<ArtifactMetadata> result;
        if (this.metadataMap == null) {
            result = (Collection<ArtifactMetadata>)Collections.emptyList();
        }
        else {
            result = this.metadataMap.values();
        }
        return result;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.getGroupId() != null) {
            sb.append(this.getGroupId());
            sb.append(":");
        }
        this.appendArtifactTypeClassifierString(sb);
        sb.append(":");
        if (this.getBaseVersionInternal() != null) {
            sb.append(this.getBaseVersionInternal());
        }
        else {
            sb.append(this.versionRange.toString());
        }
        if (this.scope != null) {
            sb.append(":");
            sb.append(this.scope);
        }
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + this.groupId.hashCode();
        result = 37 * result + this.artifactId.hashCode();
        result = 37 * result + this.type.hashCode();
        if (this.version != null) {
            result = 37 * result + this.version.hashCode();
        }
        result = 37 * result + ((this.classifier != null) ? this.classifier.hashCode() : 0);
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
        if (!a.getGroupId().equals(this.groupId)) {
            return false;
        }
        if (!a.getArtifactId().equals(this.artifactId)) {
            return false;
        }
        if (!a.getVersion().equals(this.version)) {
            return false;
        }
        if (!a.getType().equals(this.type)) {
            return false;
        }
        if (a.getClassifier() == null) {
            if (this.classifier == null) {
                return true;
            }
        }
        else if (a.getClassifier().equals(this.classifier)) {
            return true;
        }
        return false;
    }
    
    public String getBaseVersion() {
        if (this.baseVersion == null) {
            if (this.version == null) {
                throw new NullPointerException("version was null for " + this.groupId + ":" + this.artifactId);
            }
            this.setBaseVersionInternal(this.version);
        }
        return this.baseVersion;
    }
    
    protected String getBaseVersionInternal() {
        if (this.baseVersion == null && this.version != null) {
            this.setBaseVersionInternal(this.version);
        }
        return this.baseVersion;
    }
    
    public void setBaseVersion(final String baseVersion) {
        this.setBaseVersionInternal(baseVersion);
    }
    
    protected void setBaseVersionInternal(final String baseVersion) {
        final Matcher m = DefaultArtifact.VERSION_FILE_PATTERN.matcher(baseVersion);
        if (m.matches()) {
            this.baseVersion = m.group(1) + "-" + "SNAPSHOT";
        }
        else {
            this.baseVersion = baseVersion;
        }
    }
    
    public int compareTo(final Artifact a) {
        int result = this.groupId.compareTo(a.getGroupId());
        if (result == 0) {
            result = this.artifactId.compareTo(a.getArtifactId());
            if (result == 0) {
                result = this.type.compareTo(a.getType());
                if (result == 0) {
                    if (this.classifier == null) {
                        if (a.getClassifier() != null) {
                            result = 1;
                        }
                    }
                    else if (a.getClassifier() != null) {
                        result = this.classifier.compareTo(a.getClassifier());
                    }
                    else {
                        result = -1;
                    }
                    if (result == 0) {
                        result = this.version.compareTo(a.getVersion());
                    }
                }
            }
        }
        return result;
    }
    
    public void updateVersion(final String version, final ArtifactRepository localRepository) {
        this.setResolvedVersion(version);
        this.setFile(new File(localRepository.getBasedir(), localRepository.pathOf(this)));
    }
    
    public String getDownloadUrl() {
        return this.downloadUrl;
    }
    
    public void setDownloadUrl(final String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
    
    public ArtifactFilter getDependencyFilter() {
        return this.dependencyFilter;
    }
    
    public void setDependencyFilter(final ArtifactFilter artifactFilter) {
        this.dependencyFilter = artifactFilter;
    }
    
    public ArtifactHandler getArtifactHandler() {
        return this.artifactHandler;
    }
    
    public List<String> getDependencyTrail() {
        return this.dependencyTrail;
    }
    
    public void setDependencyTrail(final List<String> dependencyTrail) {
        this.dependencyTrail = dependencyTrail;
    }
    
    public void setScope(final String scope) {
        this.scope = scope;
    }
    
    public VersionRange getVersionRange() {
        if (this.versionRange == null) {
            this.versionRange = VersionRange.createFromVersion(this.version);
        }
        return this.versionRange;
    }
    
    public void setVersionRange(final VersionRange versionRange) {
        this.versionRange = versionRange;
        this.selectVersionFromNewRangeIfAvailable();
    }
    
    private void selectVersionFromNewRangeIfAvailable() {
        if (this.versionRange != null && this.versionRange.getRecommendedVersion() != null) {
            this.selectVersion(this.versionRange.getRecommendedVersion().toString());
        }
        else {
            this.version = null;
            this.baseVersion = null;
        }
    }
    
    public void selectVersion(final String version) {
        this.setBaseVersionInternal(this.version = version);
    }
    
    public void setGroupId(final String groupId) {
        this.groupId = groupId;
    }
    
    public void setArtifactId(final String artifactId) {
        this.artifactId = artifactId;
    }
    
    public boolean isSnapshot() {
        return this.getBaseVersion() != null && (this.getBaseVersion().endsWith("SNAPSHOT") || this.getBaseVersion().equals("LATEST"));
    }
    
    public void setResolved(final boolean resolved) {
        this.resolved = resolved;
    }
    
    public boolean isResolved() {
        return this.resolved;
    }
    
    public void setResolvedVersion(final String version) {
        this.version = version;
    }
    
    public void setArtifactHandler(final ArtifactHandler artifactHandler) {
        this.artifactHandler = artifactHandler;
    }
    
    public void setRelease(final boolean release) {
        this.release = release;
    }
    
    public boolean isRelease() {
        return this.release;
    }
    
    public List<ArtifactVersion> getAvailableVersions() {
        return this.availableVersions;
    }
    
    public void setAvailableVersions(final List<ArtifactVersion> availableVersions) {
        this.availableVersions = availableVersions;
    }
    
    public boolean isOptional() {
        return this.optional;
    }
    
    public ArtifactVersion getSelectedVersion() throws OverConstrainedVersionException {
        return this.versionRange.getSelectedVersion(this);
    }
    
    public boolean isSelectedVersionKnown() throws OverConstrainedVersionException {
        return this.versionRange.isSelectedVersionKnown(this);
    }
    
    public void setOptional(final boolean optional) {
        this.optional = optional;
    }
}
