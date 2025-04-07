// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact;

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
import java.util.regex.Pattern;

public interface Artifact extends Comparable<Artifact>
{
    public static final String LATEST_VERSION = "LATEST";
    public static final String SNAPSHOT_VERSION = "SNAPSHOT";
    public static final Pattern VERSION_FILE_PATTERN = Pattern.compile("^(.*)-([0-9]{8}.[0-9]{6})-([0-9]+)$");
    public static final String SCOPE_COMPILE = "compile";
    public static final String SCOPE_TEST = "test";
    public static final String SCOPE_RUNTIME = "runtime";
    public static final String SCOPE_PROVIDED = "provided";
    public static final String SCOPE_SYSTEM = "system";
    public static final String SCOPE_IMPORT = "import";
    public static final String RELEASE_VERSION = "RELEASE";
    
    String getGroupId();
    
    String getArtifactId();
    
    String getVersion();
    
    void setVersion(final String p0);
    
    String getScope();
    
    String getType();
    
    String getClassifier();
    
    boolean hasClassifier();
    
    File getFile();
    
    void setFile(final File p0);
    
    String getBaseVersion();
    
    void setBaseVersion(final String p0);
    
    String getId();
    
    String getDependencyConflictId();
    
    void addMetadata(final ArtifactMetadata p0);
    
    ArtifactMetadata getMetadata(final Class<?> p0);
    
    Collection<ArtifactMetadata> getMetadataList();
    
    void setRepository(final ArtifactRepository p0);
    
    ArtifactRepository getRepository();
    
    void updateVersion(final String p0, final ArtifactRepository p1);
    
    String getDownloadUrl();
    
    void setDownloadUrl(final String p0);
    
    ArtifactFilter getDependencyFilter();
    
    void setDependencyFilter(final ArtifactFilter p0);
    
    ArtifactHandler getArtifactHandler();
    
    List<String> getDependencyTrail();
    
    void setDependencyTrail(final List<String> p0);
    
    void setScope(final String p0);
    
    VersionRange getVersionRange();
    
    void setVersionRange(final VersionRange p0);
    
    void selectVersion(final String p0);
    
    void setGroupId(final String p0);
    
    void setArtifactId(final String p0);
    
    boolean isSnapshot();
    
    void setResolved(final boolean p0);
    
    boolean isResolved();
    
    void setResolvedVersion(final String p0);
    
    void setArtifactHandler(final ArtifactHandler p0);
    
    boolean isRelease();
    
    void setRelease(final boolean p0);
    
    List<ArtifactVersion> getAvailableVersions();
    
    void setAvailableVersions(final List<ArtifactVersion> p0);
    
    boolean isOptional();
    
    void setOptional(final boolean p0);
    
    ArtifactVersion getSelectedVersion() throws OverConstrainedVersionException;
    
    boolean isSelectedVersionKnown() throws OverConstrainedVersionException;
}
