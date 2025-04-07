// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact;

import java.util.ArrayList;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Collection;
import java.util.regex.Matcher;

public final class ArtifactUtils
{
    private ArtifactUtils() {
    }
    
    public static boolean isSnapshot(final String version) {
        return version != null && (version.toUpperCase().endsWith("SNAPSHOT") || Artifact.VERSION_FILE_PATTERN.matcher(version).matches());
    }
    
    public static String toSnapshotVersion(final String version) {
        final Matcher m = Artifact.VERSION_FILE_PATTERN.matcher(version);
        if (m.matches()) {
            return m.group(1) + '-' + "SNAPSHOT";
        }
        return version;
    }
    
    public static String versionlessKey(final Artifact artifact) {
        return versionlessKey(artifact.getGroupId(), artifact.getArtifactId());
    }
    
    public static String versionlessKey(final String groupId, final String artifactId) {
        if (groupId == null) {
            throw new NullPointerException("groupId was null");
        }
        if (artifactId == null) {
            throw new NullPointerException("artifactId was null");
        }
        return groupId + ":" + artifactId;
    }
    
    public static String artifactId(final String groupId, final String artifactId, final String type, final String version) {
        return artifactId(groupId, artifactId, type, null, version);
    }
    
    public static String artifactId(final String groupId, final String artifactId, final String type, final String classifier, final String baseVersion) {
        return groupId + ":" + artifactId + ":" + type + ((classifier != null) ? (":" + classifier) : "") + ":" + baseVersion;
    }
    
    public static Map artifactMapByVersionlessId(final Collection artifacts) {
        final Map artifactMap = new LinkedHashMap();
        if (artifacts != null) {
            for (final Artifact artifact : artifacts) {
                artifactMap.put(versionlessKey(artifact), artifact);
            }
        }
        return artifactMap;
    }
    
    public static Map artifactMapByArtifactId(final Collection artifacts) {
        final Map artifactMap = new LinkedHashMap();
        if (artifacts != null) {
            for (final Artifact artifact : artifacts) {
                artifactMap.put(artifact.getId(), artifact);
            }
        }
        return artifactMap;
    }
    
    public static Artifact copyArtifact(final Artifact artifact) {
        final VersionRange range = artifact.getVersionRange();
        final DefaultArtifact clone = new DefaultArtifact(artifact.getGroupId(), artifact.getArtifactId(), range.cloneOf(), artifact.getScope(), artifact.getType(), artifact.getClassifier(), artifact.getArtifactHandler(), artifact.isOptional());
        clone.setRelease(artifact.isRelease());
        clone.setResolvedVersion(artifact.getVersion());
        clone.setResolved(artifact.isResolved());
        clone.setFile(artifact.getFile());
        clone.setAvailableVersions(copyList(artifact.getAvailableVersions()));
        clone.setBaseVersion(artifact.getBaseVersion());
        clone.setDependencyFilter(artifact.getDependencyFilter());
        clone.setDependencyTrail(copyList(artifact.getDependencyTrail()));
        clone.setDownloadUrl(artifact.getDownloadUrl());
        clone.setRepository(artifact.getRepository());
        return clone;
    }
    
    private static List copyList(final List original) {
        List copy = null;
        if (original != null) {
            copy = new ArrayList();
            if (!original.isEmpty()) {
                copy.addAll(original);
            }
        }
        return copy;
    }
}
