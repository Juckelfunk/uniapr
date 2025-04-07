// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.resolver;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import java.util.LinkedList;
import org.apache.maven.artifact.versioning.OverConstrainedVersionException;
import java.util.Iterator;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import java.util.Set;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.maven.artifact.Artifact;

public class ResolutionNode
{
    private Artifact artifact;
    private List children;
    private final List parents;
    private final int depth;
    private final ResolutionNode parent;
    private final List remoteRepositories;
    private boolean active;
    private List trail;
    
    public ResolutionNode(final Artifact artifact, final List remoteRepositories) {
        this.active = true;
        this.artifact = artifact;
        this.remoteRepositories = remoteRepositories;
        this.depth = 0;
        this.parents = Collections.EMPTY_LIST;
        this.parent = null;
    }
    
    public ResolutionNode(final Artifact artifact, final List remoteRepositories, final ResolutionNode parent) {
        this.active = true;
        this.artifact = artifact;
        this.remoteRepositories = remoteRepositories;
        this.depth = parent.depth + 1;
        (this.parents = new ArrayList()).addAll(parent.parents);
        this.parents.add(parent.getKey());
        this.parent = parent;
    }
    
    public void setArtifact(final Artifact artifact) {
        this.artifact = artifact;
    }
    
    public Artifact getArtifact() {
        return this.artifact;
    }
    
    public Object getKey() {
        return this.artifact.getDependencyConflictId();
    }
    
    public void addDependencies(final Set artifacts, final List remoteRepositories, final ArtifactFilter filter) throws CyclicDependencyException, OverConstrainedVersionException {
        if (!artifacts.isEmpty()) {
            this.children = new ArrayList(artifacts.size());
            for (final Artifact a : artifacts) {
                if (this.parents.contains(a.getDependencyConflictId())) {
                    a.setDependencyTrail(this.getDependencyTrail());
                    throw new CyclicDependencyException("A dependency has introduced a cycle", a);
                }
                this.children.add(new ResolutionNode(a, remoteRepositories, this));
            }
        }
        else {
            this.children = Collections.EMPTY_LIST;
        }
        this.trail = null;
    }
    
    public List getDependencyTrail() throws OverConstrainedVersionException {
        final List trial = this.getTrail();
        final List ret = new ArrayList(trial.size());
        for (final Artifact artifact : trial) {
            ret.add(artifact.getId());
        }
        return ret;
    }
    
    private List getTrail() throws OverConstrainedVersionException {
        if (this.trail == null) {
            final List ids = new LinkedList();
            for (ResolutionNode node = this; node != null; node = node.parent) {
                final Artifact artifact = node.getArtifact();
                if (artifact.getVersion() == null) {
                    final ArtifactVersion selected = artifact.getSelectedVersion();
                    if (selected == null) {
                        throw new OverConstrainedVersionException("Unable to get a selected Version for " + artifact.getArtifactId(), artifact);
                    }
                    artifact.selectVersion(selected.toString());
                }
                ids.add(0, artifact);
            }
            this.trail = ids;
        }
        return this.trail;
    }
    
    public boolean isResolved() {
        return this.children != null;
    }
    
    public boolean isChildOfRootNode() {
        return this.parent != null && this.parent.parent == null;
    }
    
    public Iterator getChildrenIterator() {
        return this.children.iterator();
    }
    
    public int getDepth() {
        return this.depth;
    }
    
    public List getRemoteRepositories() {
        return this.remoteRepositories;
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    public void enable() {
        this.active = true;
        if (this.children != null) {
            for (final ResolutionNode node : this.children) {
                node.enable();
            }
        }
    }
    
    public void disable() {
        this.active = false;
        if (this.children != null) {
            for (final ResolutionNode node : this.children) {
                node.disable();
            }
        }
    }
    
    public boolean filterTrail(final ArtifactFilter filter) throws OverConstrainedVersionException {
        boolean success = true;
        if (filter != null) {
            for (Iterator i = this.getTrail().iterator(); i.hasNext() && success; success = false) {
                final Artifact artifact = i.next();
                if (!filter.include(artifact)) {}
            }
        }
        return success;
    }
    
    @Override
    public String toString() {
        return this.artifact.toString() + " (" + this.depth + "; " + (this.active ? "enabled" : "disabled") + ")";
    }
}
