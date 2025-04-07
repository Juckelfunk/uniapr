// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.metadata;

import org.apache.maven.artifact.Artifact;
import java.util.List;
import java.util.Set;

public class ResolutionGroup
{
    private final Set artifacts;
    private final List resolutionRepositories;
    private final Artifact pomArtifact;
    
    public ResolutionGroup(final Artifact pomArtifact, final Set artifacts, final List resolutionRepositories) {
        this.pomArtifact = pomArtifact;
        this.artifacts = artifacts;
        this.resolutionRepositories = resolutionRepositories;
    }
    
    public Artifact getPomArtifact() {
        return this.pomArtifact;
    }
    
    public Set getArtifacts() {
        return this.artifacts;
    }
    
    public List getResolutionRepositories() {
        return this.resolutionRepositories;
    }
}
