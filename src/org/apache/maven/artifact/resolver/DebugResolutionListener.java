// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.resolver;

import java.util.HashSet;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.artifact.Artifact;
import java.util.Set;
import org.codehaus.plexus.logging.Logger;

public class DebugResolutionListener implements ResolutionListener, ResolutionListenerForDepMgmt
{
    private Logger logger;
    private String indent;
    private static Set ignoredArtifacts;
    
    public DebugResolutionListener(final Logger logger) {
        this.indent = "";
        this.logger = logger;
    }
    
    public void testArtifact(final Artifact node) {
    }
    
    public void startProcessChildren(final Artifact artifact) {
        this.indent += "  ";
    }
    
    public void endProcessChildren(final Artifact artifact) {
        this.indent = this.indent.substring(2);
    }
    
    public void includeArtifact(final Artifact artifact) {
        this.logger.debug(this.indent + artifact + " (selected for " + artifact.getScope() + ")");
    }
    
    public void omitForNearer(final Artifact omitted, final Artifact kept) {
        final String omittedVersion = omitted.getVersion();
        final String keptVersion = kept.getVersion();
        if (omittedVersion != null) {
            if (omittedVersion.equals(keptVersion)) {
                return;
            }
        }
        else if (keptVersion == null) {
            return;
        }
        this.logger.debug(this.indent + omitted + " (removed - nearer found: " + kept.getVersion() + ")");
    }
    
    public void omitForCycle(final Artifact omitted) {
        this.logger.debug(this.indent + omitted + " (removed - causes a cycle in the graph)");
    }
    
    public void updateScopeCurrentPom(final Artifact artifact, final String ignoredScope) {
        this.logger.debug(this.indent + artifact + " (not setting scope to: " + ignoredScope + "; local scope " + artifact.getScope() + " wins)");
        if (!DebugResolutionListener.ignoredArtifacts.contains(artifact)) {
            this.logger.warn("\n\tArtifact " + artifact + " retains local scope '" + artifact.getScope() + "' overriding broader scope '" + ignoredScope + "'\n" + "\tgiven by a dependency. If this is not intended, modify or remove the local scope.\n");
            DebugResolutionListener.ignoredArtifacts.add(artifact);
        }
    }
    
    public void updateScope(final Artifact artifact, final String scope) {
        this.logger.debug(this.indent + artifact + " (setting scope to: " + scope + ")");
    }
    
    public void selectVersionFromRange(final Artifact artifact) {
        this.logger.debug(this.indent + artifact + " (setting version to: " + artifact.getVersion() + " from range: " + artifact.getVersionRange() + ")");
    }
    
    public void restrictRange(final Artifact artifact, final Artifact replacement, final VersionRange newRange) {
        this.logger.debug(this.indent + artifact + " (range restricted from: " + artifact.getVersionRange() + " and: " + replacement.getVersionRange() + " to: " + newRange + " )");
    }
    
    public void manageArtifact(final Artifact artifact, final Artifact replacement) {
        String msg = this.indent + artifact;
        msg += " (";
        if (replacement.getVersion() != null) {
            msg = msg + "applying version: " + replacement.getVersion() + ";";
        }
        if (replacement.getScope() != null) {
            msg = msg + "applying scope: " + replacement.getScope();
        }
        msg += ")";
        this.logger.debug(msg);
    }
    
    public void manageArtifactVersion(final Artifact artifact, final Artifact replacement) {
        if (!replacement.getVersion().equals(artifact.getVersion())) {
            final String msg = this.indent + artifact + " (applying version: " + replacement.getVersion() + ")";
            this.logger.debug(msg);
        }
    }
    
    public void manageArtifactScope(final Artifact artifact, final Artifact replacement) {
        if (!replacement.getScope().equals(artifact.getScope())) {
            final String msg = this.indent + artifact + " (applying scope: " + replacement.getScope() + ")";
            this.logger.debug(msg);
        }
    }
    
    static {
        DebugResolutionListener.ignoredArtifacts = new HashSet();
    }
}
