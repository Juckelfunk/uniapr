// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.resolver;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class ArtifactResolutionResult
{
    private Set resolutionNodes;
    private Set artifacts;
    
    public Set getArtifacts() {
        if (this.artifacts == null) {
            this.artifacts = new LinkedHashSet();
            for (final ResolutionNode node : this.resolutionNodes) {
                this.artifacts.add(node.getArtifact());
            }
        }
        return this.artifacts;
    }
    
    public Set getArtifactResolutionNodes() {
        return this.resolutionNodes;
    }
    
    public void setArtifactResolutionNodes(final Set resolutionNodes) {
        this.resolutionNodes = resolutionNodes;
        this.artifacts = null;
    }
    
    @Override
    public String toString() {
        return "Artifacts: " + this.artifacts + " Nodes: " + this.resolutionNodes;
    }
}
