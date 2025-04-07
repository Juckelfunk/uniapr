// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.resolver;

import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.artifact.Artifact;
import org.codehaus.plexus.logging.Logger;

public class WarningResolutionListener implements ResolutionListener
{
    private Logger logger;
    
    public WarningResolutionListener(final Logger logger) {
        this.logger = logger;
    }
    
    public void testArtifact(final Artifact node) {
    }
    
    public void startProcessChildren(final Artifact artifact) {
    }
    
    public void endProcessChildren(final Artifact artifact) {
    }
    
    public void includeArtifact(final Artifact artifact) {
    }
    
    public void omitForNearer(final Artifact omitted, final Artifact kept) {
    }
    
    public void omitForCycle(final Artifact omitted) {
    }
    
    public void updateScopeCurrentPom(final Artifact artifact, final String scope) {
    }
    
    public void updateScope(final Artifact artifact, final String scope) {
    }
    
    public void manageArtifact(final Artifact artifact, final Artifact replacement) {
    }
    
    public void selectVersionFromRange(final Artifact artifact) {
    }
    
    public void restrictRange(final Artifact artifact, final Artifact replacement, final VersionRange newRange) {
    }
}
