// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.resolver;

import org.apache.maven.artifact.Artifact;

public interface ResolutionListenerForDepMgmt
{
    void manageArtifactVersion(final Artifact p0, final Artifact p1);
    
    void manageArtifactScope(final Artifact p0, final Artifact p1);
}
