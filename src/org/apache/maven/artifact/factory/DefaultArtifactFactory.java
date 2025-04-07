// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.factory;

import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;

public class DefaultArtifactFactory implements ArtifactFactory
{
    private ArtifactHandlerManager artifactHandlerManager;
    
    public Artifact createArtifact(final String groupId, final String artifactId, final String version, final String scope, final String type) {
        return this.createArtifact(groupId, artifactId, version, scope, type, null, null);
    }
    
    public Artifact createArtifactWithClassifier(final String groupId, final String artifactId, final String version, final String type, final String classifier) {
        return this.createArtifact(groupId, artifactId, version, null, type, classifier, null);
    }
    
    public Artifact createDependencyArtifact(final String groupId, final String artifactId, final VersionRange versionRange, final String type, final String classifier, final String scope) {
        return this.createArtifact(groupId, artifactId, versionRange, type, classifier, scope, null);
    }
    
    public Artifact createDependencyArtifact(final String groupId, final String artifactId, final VersionRange versionRange, final String type, final String classifier, final String scope, final boolean optional) {
        return this.createArtifact(groupId, artifactId, versionRange, type, classifier, scope, null, optional);
    }
    
    public Artifact createDependencyArtifact(final String groupId, final String artifactId, final VersionRange versionRange, final String type, final String classifier, final String scope, final String inheritedScope) {
        return this.createArtifact(groupId, artifactId, versionRange, type, classifier, scope, inheritedScope);
    }
    
    public Artifact createDependencyArtifact(final String groupId, final String artifactId, final VersionRange versionRange, final String type, final String classifier, final String scope, final String inheritedScope, final boolean optional) {
        return this.createArtifact(groupId, artifactId, versionRange, type, classifier, scope, inheritedScope, optional);
    }
    
    public Artifact createBuildArtifact(final String groupId, final String artifactId, final String version, final String packaging) {
        return this.createArtifact(groupId, artifactId, version, null, packaging, null, null);
    }
    
    public Artifact createProjectArtifact(final String groupId, final String artifactId, final String version) {
        return this.createProjectArtifact(groupId, artifactId, version, null);
    }
    
    public Artifact createParentArtifact(final String groupId, final String artifactId, final String version) {
        return this.createProjectArtifact(groupId, artifactId, version);
    }
    
    public Artifact createPluginArtifact(final String groupId, final String artifactId, final VersionRange versionRange) {
        return this.createArtifact(groupId, artifactId, versionRange, "maven-plugin", null, "runtime", null);
    }
    
    public Artifact createProjectArtifact(final String groupId, final String artifactId, final String version, final String scope) {
        return this.createArtifact(groupId, artifactId, version, scope, "pom");
    }
    
    public Artifact createExtensionArtifact(final String groupId, final String artifactId, final VersionRange versionRange) {
        return this.createArtifact(groupId, artifactId, versionRange, "jar", null, "runtime", null);
    }
    
    private Artifact createArtifact(final String groupId, final String artifactId, final String version, final String scope, final String type, final String classifier, final String inheritedScope) {
        VersionRange versionRange = null;
        if (version != null) {
            versionRange = VersionRange.createFromVersion(version);
        }
        return this.createArtifact(groupId, artifactId, versionRange, type, classifier, scope, inheritedScope);
    }
    
    private Artifact createArtifact(final String groupId, final String artifactId, final VersionRange versionRange, final String type, final String classifier, final String scope, final String inheritedScope) {
        return this.createArtifact(groupId, artifactId, versionRange, type, classifier, scope, inheritedScope, false);
    }
    
    private Artifact createArtifact(final String groupId, final String artifactId, final VersionRange versionRange, final String type, final String classifier, final String scope, final String inheritedScope, final boolean optional) {
        String desiredScope = "runtime";
        if (inheritedScope == null) {
            desiredScope = scope;
        }
        else {
            if ("test".equals(scope) || "provided".equals(scope)) {
                return null;
            }
            if ("compile".equals(scope) && "compile".equals(inheritedScope)) {
                desiredScope = "compile";
            }
        }
        if ("test".equals(inheritedScope)) {
            desiredScope = "test";
        }
        if ("provided".equals(inheritedScope)) {
            desiredScope = "provided";
        }
        if ("system".equals(scope)) {
            desiredScope = "system";
        }
        final ArtifactHandler handler = this.artifactHandlerManager.getArtifactHandler(type);
        return new DefaultArtifact(groupId, artifactId, versionRange, desiredScope, type, classifier, handler, optional);
    }
    
    protected ArtifactHandlerManager getArtifactHandlerManager() {
        return this.artifactHandlerManager;
    }
}
