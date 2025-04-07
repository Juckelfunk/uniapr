// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.factory;

import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.artifact.Artifact;

public interface ArtifactFactory
{
    public static final String ROLE = ArtifactFactory.class.getName();
    
    Artifact createArtifact(final String p0, final String p1, final String p2, final String p3, final String p4);
    
    Artifact createArtifactWithClassifier(final String p0, final String p1, final String p2, final String p3, final String p4);
    
    Artifact createDependencyArtifact(final String p0, final String p1, final VersionRange p2, final String p3, final String p4, final String p5);
    
    Artifact createDependencyArtifact(final String p0, final String p1, final VersionRange p2, final String p3, final String p4, final String p5, final boolean p6);
    
    Artifact createDependencyArtifact(final String p0, final String p1, final VersionRange p2, final String p3, final String p4, final String p5, final String p6);
    
    Artifact createDependencyArtifact(final String p0, final String p1, final VersionRange p2, final String p3, final String p4, final String p5, final String p6, final boolean p7);
    
    Artifact createBuildArtifact(final String p0, final String p1, final String p2, final String p3);
    
    Artifact createProjectArtifact(final String p0, final String p1, final String p2);
    
    Artifact createParentArtifact(final String p0, final String p1, final String p2);
    
    Artifact createPluginArtifact(final String p0, final String p1, final VersionRange p2);
    
    Artifact createProjectArtifact(final String p0, final String p1, final String p2, final String p3);
    
    Artifact createExtensionArtifact(final String p0, final String p1, final VersionRange p2);
}
