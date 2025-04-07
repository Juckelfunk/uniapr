// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.resolver;

import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.artifact.Artifact;

public interface ResolutionListener
{
    public static final String ROLE = ResolutionListener.class.getName();
    public static final int TEST_ARTIFACT = 1;
    public static final int PROCESS_CHILDREN = 2;
    public static final int FINISH_PROCESSING_CHILDREN = 3;
    public static final int INCLUDE_ARTIFACT = 4;
    public static final int OMIT_FOR_NEARER = 5;
    public static final int UPDATE_SCOPE = 6;
    @Deprecated
    public static final int MANAGE_ARTIFACT = 7;
    public static final int OMIT_FOR_CYCLE = 8;
    public static final int UPDATE_SCOPE_CURRENT_POM = 9;
    public static final int SELECT_VERSION_FROM_RANGE = 10;
    public static final int RESTRICT_RANGE = 11;
    public static final int MANAGE_ARTIFACT_VERSION = 12;
    public static final int MANAGE_ARTIFACT_SCOPE = 13;
    
    void testArtifact(final Artifact p0);
    
    void startProcessChildren(final Artifact p0);
    
    void endProcessChildren(final Artifact p0);
    
    void includeArtifact(final Artifact p0);
    
    void omitForNearer(final Artifact p0, final Artifact p1);
    
    void updateScope(final Artifact p0, final String p1);
    
    @Deprecated
    void manageArtifact(final Artifact p0, final Artifact p1);
    
    void omitForCycle(final Artifact p0);
    
    void updateScopeCurrentPom(final Artifact p0, final String p1);
    
    void selectVersionFromRange(final Artifact p0);
    
    void restrictRange(final Artifact p0, final Artifact p1, final VersionRange p2);
}
