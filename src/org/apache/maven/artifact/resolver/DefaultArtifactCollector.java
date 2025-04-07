// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.resolver;

import org.apache.maven.artifact.metadata.ResolutionGroup;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.artifact.resolver.filter.AndArtifactFilter;
import java.util.ArrayList;
import org.apache.maven.artifact.versioning.OverConstrainedVersionException;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import java.util.Iterator;
import org.apache.maven.artifact.versioning.ManagedVersionMap;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Collections;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import java.util.List;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.Artifact;
import java.util.Set;

public class DefaultArtifactCollector implements ArtifactCollector
{
    public ArtifactResolutionResult collect(final Set artifacts, final Artifact originatingArtifact, final ArtifactRepository localRepository, final List remoteRepositories, final ArtifactMetadataSource source, final ArtifactFilter filter, final List listeners) throws ArtifactResolutionException {
        return this.collect(artifacts, originatingArtifact, Collections.EMPTY_MAP, localRepository, remoteRepositories, source, filter, listeners);
    }
    
    public ArtifactResolutionResult collect(final Set artifacts, final Artifact originatingArtifact, final Map managedVersions, final ArtifactRepository localRepository, final List remoteRepositories, final ArtifactMetadataSource source, final ArtifactFilter filter, final List listeners) throws ArtifactResolutionException {
        final Map resolvedArtifacts = new LinkedHashMap();
        final ResolutionNode root = new ResolutionNode(originatingArtifact, remoteRepositories);
        root.addDependencies(artifacts, remoteRepositories, filter);
        final ManagedVersionMap versionMap = this.getManagedVersionsMap(originatingArtifact, managedVersions);
        this.recurse(originatingArtifact, root, resolvedArtifacts, versionMap, localRepository, remoteRepositories, source, filter, listeners);
        final Set set = new LinkedHashSet();
        for (final List nodes : resolvedArtifacts.values()) {
            for (final ResolutionNode node : nodes) {
                if (!node.equals(root) && node.isActive()) {
                    final Artifact artifact = node.getArtifact();
                    if (!node.filterTrail(filter) || (!node.isChildOfRootNode() && artifact.isOptional())) {
                        continue;
                    }
                    artifact.setDependencyTrail(node.getDependencyTrail());
                    set.add(node);
                }
            }
        }
        final ArtifactResolutionResult result = new ArtifactResolutionResult();
        result.setArtifactResolutionNodes(set);
        return result;
    }
    
    private ManagedVersionMap getManagedVersionsMap(final Artifact originatingArtifact, final Map managedVersions) {
        ManagedVersionMap versionMap;
        if (managedVersions != null && managedVersions instanceof ManagedVersionMap) {
            versionMap = (ManagedVersionMap)managedVersions;
        }
        else {
            versionMap = new ManagedVersionMap(managedVersions);
        }
        final Artifact managedOriginatingArtifact = versionMap.get(originatingArtifact.getDependencyConflictId());
        if (managedOriginatingArtifact != null) {
            if (managedVersions instanceof ManagedVersionMap) {
                versionMap = new ManagedVersionMap(managedVersions);
            }
            versionMap.remove(originatingArtifact.getDependencyConflictId());
        }
        return versionMap;
    }
    
    private void recurse(final Artifact originatingArtifact, final ResolutionNode node, final Map resolvedArtifacts, final ManagedVersionMap managedVersions, final ArtifactRepository localRepository, final List remoteRepositories, final ArtifactMetadataSource source, final ArtifactFilter filter, final List listeners) throws CyclicDependencyException, ArtifactResolutionException, OverConstrainedVersionException {
        this.fireEvent(1, listeners, node);
        final Object key = node.getKey();
        if (managedVersions.containsKey(key)) {
            this.manageArtifact(node, managedVersions, listeners);
        }
        List previousNodes = resolvedArtifacts.get(key);
        if (previousNodes != null) {
            for (final ResolutionNode previous : previousNodes) {
                if (previous.isActive()) {
                    final VersionRange previousRange = previous.getArtifact().getVersionRange();
                    final VersionRange currentRange = node.getArtifact().getVersionRange();
                    if (previousRange != null && currentRange != null) {
                        final VersionRange newRange = previousRange.restrict(currentRange);
                        if (newRange.isSelectedVersionKnown(previous.getArtifact())) {
                            this.fireEvent(11, listeners, node, previous.getArtifact(), newRange);
                        }
                        previous.getArtifact().setVersionRange(newRange);
                        node.getArtifact().setVersionRange(currentRange.restrict(previousRange));
                        final ResolutionNode[] resetNodes = { previous, node };
                        for (int j = 0; j < 2; ++j) {
                            final Artifact resetArtifact = resetNodes[j].getArtifact();
                            if (resetArtifact.getVersion() == null && resetArtifact.getVersionRange() != null) {
                                List versions = resetArtifact.getAvailableVersions();
                                if (versions == null) {
                                    try {
                                        versions = source.retrieveAvailableVersions(resetArtifact, localRepository, remoteRepositories);
                                        resetArtifact.setAvailableVersions(versions);
                                    }
                                    catch (ArtifactMetadataRetrievalException e) {
                                        resetArtifact.setDependencyTrail(node.getDependencyTrail());
                                        throw new ArtifactResolutionException("Unable to get dependency information: " + e.getMessage(), resetArtifact, remoteRepositories, e);
                                    }
                                }
                                final ArtifactVersion selectedVersion = resetArtifact.getVersionRange().matchVersion(resetArtifact.getAvailableVersions());
                                if (selectedVersion == null) {
                                    throw new OverConstrainedVersionException(" Unable to find a version in " + resetArtifact.getAvailableVersions() + " to match the range " + resetArtifact.getVersionRange(), resetArtifact);
                                }
                                resetArtifact.selectVersion(selectedVersion.toString());
                                this.fireEvent(10, listeners, resetNodes[j]);
                            }
                        }
                    }
                    ResolutionNode nearest;
                    ResolutionNode farthest;
                    if (previous.getDepth() <= node.getDepth()) {
                        nearest = previous;
                        farthest = node;
                    }
                    else {
                        nearest = node;
                        farthest = previous;
                    }
                    if (this.checkScopeUpdate(farthest, nearest, listeners)) {
                        nearest.disable();
                        farthest.getArtifact().setVersion(nearest.getArtifact().getVersion());
                        this.fireEvent(5, listeners, nearest, farthest.getArtifact());
                    }
                    else {
                        farthest.disable();
                        this.fireEvent(5, listeners, farthest, nearest.getArtifact());
                    }
                }
            }
        }
        else {
            previousNodes = new ArrayList();
            resolvedArtifacts.put(key, previousNodes);
        }
        previousNodes.add(node);
        if (node.isActive()) {
            this.fireEvent(4, listeners, node);
        }
        if (node.isActive() && !"system".equals(node.getArtifact().getScope())) {
            this.fireEvent(2, listeners, node);
            final Artifact parentArtifact = node.getArtifact();
            final Iterator k = node.getChildrenIterator();
            while (k.hasNext()) {
                final ResolutionNode child = k.next();
                if (!child.isResolved() && (!child.getArtifact().isOptional() || child.isChildOfRootNode())) {
                    Artifact artifact = child.getArtifact();
                    artifact.setDependencyTrail(node.getDependencyTrail());
                    final List childRemoteRepositories = child.getRemoteRepositories();
                    try {
                        Object childKey;
                        do {
                            childKey = child.getKey();
                            if (managedVersions.containsKey(childKey)) {
                                this.manageArtifact(child, managedVersions, listeners);
                                final Artifact ma = managedVersions.get(childKey);
                                final ArtifactFilter managedExclusionFilter = ma.getDependencyFilter();
                                if (null != managedExclusionFilter) {
                                    if (null != artifact.getDependencyFilter()) {
                                        final AndArtifactFilter aaf = new AndArtifactFilter();
                                        aaf.add(artifact.getDependencyFilter());
                                        aaf.add(managedExclusionFilter);
                                        artifact.setDependencyFilter(aaf);
                                    }
                                    else {
                                        artifact.setDependencyFilter(managedExclusionFilter);
                                    }
                                }
                            }
                            if (artifact.getVersion() == null) {
                                ArtifactVersion version;
                                if (artifact.isSelectedVersionKnown()) {
                                    version = artifact.getSelectedVersion();
                                }
                                else {
                                    List versions2 = artifact.getAvailableVersions();
                                    if (versions2 == null) {
                                        versions2 = source.retrieveAvailableVersions(artifact, localRepository, childRemoteRepositories);
                                        artifact.setAvailableVersions(versions2);
                                    }
                                    Collections.sort((List<Comparable>)versions2);
                                    final VersionRange versionRange = artifact.getVersionRange();
                                    version = versionRange.matchVersion(versions2);
                                    if (version == null) {
                                        if (versions2.isEmpty()) {
                                            throw new OverConstrainedVersionException("No versions are present in the repository for the artifact with a range " + versionRange, artifact, childRemoteRepositories);
                                        }
                                        throw new OverConstrainedVersionException("Couldn't find a version in " + versions2 + " to match range " + versionRange, artifact, childRemoteRepositories);
                                    }
                                }
                                artifact.selectVersion(version.toString());
                                this.fireEvent(10, listeners, child);
                            }
                            final Artifact relocated = source.retrieveRelocatedArtifact(artifact, localRepository, childRemoteRepositories);
                            if (relocated != null && !artifact.equals(relocated)) {
                                relocated.setDependencyFilter(artifact.getDependencyFilter());
                                artifact = relocated;
                                child.setArtifact(artifact);
                            }
                        } while (!childKey.equals(child.getKey()));
                        if (parentArtifact != null && parentArtifact.getDependencyFilter() != null && !parentArtifact.getDependencyFilter().include(artifact)) {
                            continue;
                        }
                        final ResolutionGroup rGroup = source.retrieve(artifact, localRepository, childRemoteRepositories);
                        if (rGroup == null) {
                            continue;
                        }
                        child.addDependencies(rGroup.getArtifacts(), rGroup.getResolutionRepositories(), filter);
                    }
                    catch (CyclicDependencyException e2) {
                        this.fireEvent(8, listeners, new ResolutionNode(e2.getArtifact(), childRemoteRepositories, child));
                    }
                    catch (ArtifactMetadataRetrievalException e3) {
                        artifact.setDependencyTrail(node.getDependencyTrail());
                        throw new ArtifactResolutionException("Unable to get dependency information: " + e3.getMessage(), artifact, childRemoteRepositories, e3);
                    }
                    this.recurse(originatingArtifact, child, resolvedArtifacts, managedVersions, localRepository, childRemoteRepositories, source, filter, listeners);
                }
            }
            this.fireEvent(3, listeners, node);
        }
    }
    
    private void manageArtifact(final ResolutionNode node, final ManagedVersionMap managedVersions, final List listeners) {
        final Artifact artifact = managedVersions.get(node.getKey());
        if (artifact.getVersion() != null && (!node.isChildOfRootNode() || node.getArtifact().getVersion() == null)) {
            this.fireEvent(12, listeners, node, artifact);
            node.getArtifact().setVersion(artifact.getVersion());
        }
        if (artifact.getScope() != null && (!node.isChildOfRootNode() || node.getArtifact().getScope() == null)) {
            this.fireEvent(13, listeners, node, artifact);
            node.getArtifact().setScope(artifact.getScope());
        }
    }
    
    boolean checkScopeUpdate(final ResolutionNode farthest, final ResolutionNode nearest, final List listeners) {
        boolean updateScope = false;
        final Artifact farthestArtifact = farthest.getArtifact();
        final Artifact nearestArtifact = nearest.getArtifact();
        if ("runtime".equals(farthestArtifact.getScope()) && ("test".equals(nearestArtifact.getScope()) || "provided".equals(nearestArtifact.getScope()))) {
            updateScope = true;
        }
        if ("compile".equals(farthestArtifact.getScope()) && !"compile".equals(nearestArtifact.getScope())) {
            updateScope = true;
        }
        if (nearest.getDepth() < 2 && updateScope) {
            updateScope = false;
            this.fireEvent(9, listeners, nearest, farthestArtifact);
        }
        if (updateScope) {
            this.fireEvent(6, listeners, nearest, farthestArtifact);
            nearestArtifact.setScope(farthestArtifact.getScope());
        }
        return updateScope;
    }
    
    private void fireEvent(final int event, final List listeners, final ResolutionNode node) {
        this.fireEvent(event, listeners, node, null);
    }
    
    private void fireEvent(final int event, final List listeners, final ResolutionNode node, final Artifact replacement) {
        this.fireEvent(event, listeners, node, replacement, null);
    }
    
    private void fireEvent(final int event, final List listeners, final ResolutionNode node, final Artifact replacement, final VersionRange newRange) {
        for (final ResolutionListener listener : listeners) {
            switch (event) {
                case 1: {
                    listener.testArtifact(node.getArtifact());
                    continue;
                }
                case 2: {
                    listener.startProcessChildren(node.getArtifact());
                    continue;
                }
                case 3: {
                    listener.endProcessChildren(node.getArtifact());
                    continue;
                }
                case 4: {
                    listener.includeArtifact(node.getArtifact());
                    continue;
                }
                case 5: {
                    listener.omitForNearer(node.getArtifact(), replacement);
                    continue;
                }
                case 8: {
                    listener.omitForCycle(node.getArtifact());
                    continue;
                }
                case 6: {
                    listener.updateScope(node.getArtifact(), replacement.getScope());
                    continue;
                }
                case 9: {
                    listener.updateScopeCurrentPom(node.getArtifact(), replacement.getScope());
                    continue;
                }
                case 12: {
                    if (listener instanceof ResolutionListenerForDepMgmt) {
                        final ResolutionListenerForDepMgmt asImpl = (ResolutionListenerForDepMgmt)listener;
                        asImpl.manageArtifactVersion(node.getArtifact(), replacement);
                        continue;
                    }
                    listener.manageArtifact(node.getArtifact(), replacement);
                    continue;
                }
                case 13: {
                    if (listener instanceof ResolutionListenerForDepMgmt) {
                        final ResolutionListenerForDepMgmt asImpl = (ResolutionListenerForDepMgmt)listener;
                        asImpl.manageArtifactScope(node.getArtifact(), replacement);
                        continue;
                    }
                    listener.manageArtifact(node.getArtifact(), replacement);
                    continue;
                }
                case 10: {
                    listener.selectVersionFromRange(node.getArtifact());
                    continue;
                }
                case 11: {
                    if (node.getArtifact().getVersionRange().hasRestrictions() || replacement.getVersionRange().hasRestrictions()) {
                        listener.restrictRange(node.getArtifact(), replacement, newRange);
                        continue;
                    }
                    continue;
                }
                default: {
                    throw new IllegalStateException("Unknown event: " + event);
                }
            }
        }
    }
}
