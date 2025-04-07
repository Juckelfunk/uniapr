// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project.inheritance;

import java.util.StringTokenizer;
import java.util.LinkedList;
import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.Site;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Scm;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.PluginContainer;
import org.apache.maven.model.Build;
import org.codehaus.plexus.util.StringUtils;
import org.apache.maven.model.Reporting;
import java.util.Iterator;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Dependency;
import java.util.TreeMap;
import java.util.Map;
import java.util.Properties;
import org.apache.maven.model.Repository;
import java.util.List;
import org.apache.maven.project.ModelUtils;
import org.apache.maven.model.Model;

public class DefaultModelInheritanceAssembler implements ModelInheritanceAssembler
{
    public void copyModel(final Model dest, final Model source) {
        this.assembleModelInheritance(dest, source, null, false);
    }
    
    public void assembleModelInheritance(final Model child, final Model parent, final String childPathAdjustment) {
        this.assembleModelInheritance(child, parent, childPathAdjustment, true);
    }
    
    public void assembleModelInheritance(final Model child, final Model parent) {
        this.assembleModelInheritance(child, parent, null, true);
    }
    
    private void assembleModelInheritance(final Model child, final Model parent, final String childPathAdjustment, final boolean appendPaths) {
        if (parent == null) {
            return;
        }
        if (child.getGroupId() == null) {
            child.setGroupId(parent.getGroupId());
        }
        if (child.getVersion() == null && child.getParent() != null) {
            child.setVersion(child.getParent().getVersion());
        }
        if (child.getInceptionYear() == null) {
            child.setInceptionYear(parent.getInceptionYear());
        }
        if (child.getUrl() == null) {
            if (parent.getUrl() != null) {
                child.setUrl(this.appendPath(parent.getUrl(), child.getArtifactId(), childPathAdjustment, appendPaths));
            }
            else {
                child.setUrl(parent.getUrl());
            }
        }
        this.assembleDistributionInheritence(child, parent, childPathAdjustment, appendPaths);
        if (child.getIssueManagement() == null) {
            child.setIssueManagement(parent.getIssueManagement());
        }
        if (child.getDescription() == null) {
            child.setDescription(parent.getDescription());
        }
        if (child.getOrganization() == null) {
            child.setOrganization(parent.getOrganization());
        }
        this.assembleScmInheritance(child, parent, childPathAdjustment, appendPaths);
        if (child.getCiManagement() == null) {
            child.setCiManagement(parent.getCiManagement());
        }
        if (child.getDevelopers().size() == 0) {
            child.setDevelopers(parent.getDevelopers());
        }
        if (child.getLicenses().size() == 0) {
            child.setLicenses(parent.getLicenses());
        }
        if (child.getContributors().size() == 0) {
            child.setContributors(parent.getContributors());
        }
        if (child.getMailingLists().size() == 0) {
            child.setMailingLists(parent.getMailingLists());
        }
        this.assembleBuildInheritance(child, parent);
        this.assembleDependencyInheritance(child, parent);
        child.setRepositories(ModelUtils.mergeRepositoryLists(child.getRepositories(), parent.getRepositories()));
        child.setPluginRepositories(ModelUtils.mergeRepositoryLists(child.getPluginRepositories(), parent.getPluginRepositories()));
        this.assembleReportingInheritance(child, parent);
        this.assembleDependencyManagementInheritance(child, parent);
        final Properties props = new Properties();
        props.putAll(parent.getProperties());
        props.putAll(child.getProperties());
        child.setProperties(props);
    }
    
    private void assembleDependencyManagementInheritance(final Model child, final Model parent) {
        final DependencyManagement parentDepMgmt = parent.getDependencyManagement();
        final DependencyManagement childDepMgmt = child.getDependencyManagement();
        if (parentDepMgmt != null) {
            if (childDepMgmt == null) {
                child.setDependencyManagement(parentDepMgmt);
            }
            else {
                final List childDeps = childDepMgmt.getDependencies();
                final Map mappedChildDeps = new TreeMap();
                for (final Dependency dep : childDeps) {
                    mappedChildDeps.put(dep.getManagementKey(), dep);
                }
                for (final Dependency dep : parentDepMgmt.getDependencies()) {
                    if (!mappedChildDeps.containsKey(dep.getManagementKey())) {
                        childDepMgmt.addDependency(dep);
                    }
                }
            }
        }
    }
    
    private void assembleReportingInheritance(final Model child, final Model parent) {
        Reporting childReporting = child.getReporting();
        final Reporting parentReporting = parent.getReporting();
        if (parentReporting != null) {
            if (childReporting == null) {
                childReporting = new Reporting();
                child.setReporting(childReporting);
            }
            if (childReporting.isExcludeDefaultsValue() == null) {
                childReporting.setExcludeDefaultsValue(parentReporting.isExcludeDefaultsValue());
            }
            if (StringUtils.isEmpty(childReporting.getOutputDirectory())) {
                childReporting.setOutputDirectory(parentReporting.getOutputDirectory());
            }
            ModelUtils.mergeReportPluginLists(childReporting, parentReporting, true);
        }
    }
    
    private void assembleDependencyInheritance(final Model child, final Model parent) {
        child.setDependencies(ModelUtils.mergeDependencyList(child.getDependencies(), parent.getDependencies()));
    }
    
    private void assembleBuildInheritance(final Model child, final Model parent) {
        Build childBuild = child.getBuild();
        final Build parentBuild = parent.getBuild();
        if (parentBuild != null) {
            if (childBuild == null) {
                childBuild = new Build();
                child.setBuild(childBuild);
            }
            this.assembleBuildInheritance(childBuild, parentBuild, true);
        }
    }
    
    public void assembleBuildInheritance(final Build childBuild, final Build parentBuild, final boolean handleAsInheritance) {
        if (childBuild.getSourceDirectory() == null) {
            childBuild.setSourceDirectory(parentBuild.getSourceDirectory());
        }
        if (childBuild.getScriptSourceDirectory() == null) {
            childBuild.setScriptSourceDirectory(parentBuild.getScriptSourceDirectory());
        }
        if (childBuild.getTestSourceDirectory() == null) {
            childBuild.setTestSourceDirectory(parentBuild.getTestSourceDirectory());
        }
        if (childBuild.getOutputDirectory() == null) {
            childBuild.setOutputDirectory(parentBuild.getOutputDirectory());
        }
        if (childBuild.getTestOutputDirectory() == null) {
            childBuild.setTestOutputDirectory(parentBuild.getTestOutputDirectory());
        }
        ModelUtils.mergeExtensionLists(childBuild, parentBuild);
        if (childBuild.getDirectory() == null) {
            childBuild.setDirectory(parentBuild.getDirectory());
        }
        if (childBuild.getDefaultGoal() == null) {
            childBuild.setDefaultGoal(parentBuild.getDefaultGoal());
        }
        if (childBuild.getFinalName() == null) {
            childBuild.setFinalName(parentBuild.getFinalName());
        }
        ModelUtils.mergeFilterLists(childBuild.getFilters(), parentBuild.getFilters());
        List resources = childBuild.getResources();
        if (resources == null || resources.isEmpty()) {
            childBuild.setResources(parentBuild.getResources());
        }
        resources = childBuild.getTestResources();
        if (resources == null || resources.isEmpty()) {
            childBuild.setTestResources(parentBuild.getTestResources());
        }
        ModelUtils.mergePluginLists(childBuild, parentBuild, handleAsInheritance);
        final PluginManagement dominantPM = childBuild.getPluginManagement();
        final PluginManagement recessivePM = parentBuild.getPluginManagement();
        if (dominantPM == null && recessivePM != null) {
            childBuild.setPluginManagement(recessivePM);
        }
        else {
            ModelUtils.mergePluginLists(childBuild.getPluginManagement(), parentBuild.getPluginManagement(), false);
        }
    }
    
    private void assembleScmInheritance(final Model child, final Model parent, final String childPathAdjustment, final boolean appendPaths) {
        if (parent.getScm() != null) {
            final Scm parentScm = parent.getScm();
            Scm childScm = child.getScm();
            if (childScm == null) {
                childScm = new Scm();
                child.setScm(childScm);
            }
            if (StringUtils.isEmpty(childScm.getConnection()) && !StringUtils.isEmpty(parentScm.getConnection())) {
                childScm.setConnection(this.appendPath(parentScm.getConnection(), child.getArtifactId(), childPathAdjustment, appendPaths));
            }
            if (StringUtils.isEmpty(childScm.getDeveloperConnection()) && !StringUtils.isEmpty(parentScm.getDeveloperConnection())) {
                childScm.setDeveloperConnection(this.appendPath(parentScm.getDeveloperConnection(), child.getArtifactId(), childPathAdjustment, appendPaths));
            }
            if (StringUtils.isEmpty(childScm.getUrl()) && !StringUtils.isEmpty(parentScm.getUrl())) {
                childScm.setUrl(this.appendPath(parentScm.getUrl(), child.getArtifactId(), childPathAdjustment, appendPaths));
            }
        }
    }
    
    private void assembleDistributionInheritence(final Model child, final Model parent, final String childPathAdjustment, final boolean appendPaths) {
        if (parent.getDistributionManagement() != null) {
            final DistributionManagement parentDistMgmt = parent.getDistributionManagement();
            DistributionManagement childDistMgmt = child.getDistributionManagement();
            if (childDistMgmt == null) {
                childDistMgmt = new DistributionManagement();
                child.setDistributionManagement(childDistMgmt);
            }
            if (childDistMgmt.getSite() == null && parentDistMgmt.getSite() != null) {
                final Site site = new Site();
                childDistMgmt.setSite(site);
                site.setId(parentDistMgmt.getSite().getId());
                site.setName(parentDistMgmt.getSite().getName());
                site.setUrl(parentDistMgmt.getSite().getUrl());
                if (site.getUrl() != null) {
                    site.setUrl(this.appendPath(site.getUrl(), child.getArtifactId(), childPathAdjustment, appendPaths));
                }
            }
            if (childDistMgmt.getRepository() == null && parentDistMgmt.getRepository() != null) {
                final DeploymentRepository repository = copyDistributionRepository(parentDistMgmt.getRepository());
                childDistMgmt.setRepository(repository);
            }
            if (childDistMgmt.getSnapshotRepository() == null && parentDistMgmt.getSnapshotRepository() != null) {
                final DeploymentRepository repository = copyDistributionRepository(parentDistMgmt.getSnapshotRepository());
                childDistMgmt.setSnapshotRepository(repository);
            }
            if (StringUtils.isEmpty(childDistMgmt.getDownloadUrl())) {
                childDistMgmt.setDownloadUrl(parentDistMgmt.getDownloadUrl());
            }
        }
    }
    
    private static DeploymentRepository copyDistributionRepository(final DeploymentRepository parentRepository) {
        final DeploymentRepository repository = new DeploymentRepository();
        repository.setId(parentRepository.getId());
        repository.setName(parentRepository.getName());
        repository.setUrl(parentRepository.getUrl());
        repository.setLayout(parentRepository.getLayout());
        repository.setUniqueVersion(parentRepository.isUniqueVersion());
        return repository;
    }
    
    protected String appendPath(final String parentPath, final String childPath, final String pathAdjustment, final boolean appendPaths) {
        String uncleanPath = parentPath;
        if (appendPaths) {
            if (pathAdjustment != null) {
                uncleanPath = uncleanPath + "/" + pathAdjustment;
            }
            if (childPath != null) {
                uncleanPath = uncleanPath + "/" + childPath;
            }
        }
        String cleanedPath = "";
        final int protocolIdx = uncleanPath.indexOf("://");
        if (protocolIdx > -1) {
            cleanedPath = uncleanPath.substring(0, protocolIdx + 3);
            uncleanPath = uncleanPath.substring(protocolIdx + 3);
        }
        if (uncleanPath.startsWith("//")) {
            cleanedPath += "//";
        }
        else if (uncleanPath.startsWith("/")) {
            cleanedPath += "/";
        }
        return cleanedPath + resolvePath(uncleanPath);
    }
    
    private static String resolvePath(final String uncleanPath) {
        final LinkedList pathElements = new LinkedList();
        final StringTokenizer tokenizer = new StringTokenizer(uncleanPath, "/");
        while (tokenizer.hasMoreTokens()) {
            final String token = tokenizer.nextToken();
            if (token.equals("")) {
                continue;
            }
            if (token.equals("..")) {
                if (pathElements.isEmpty()) {
                    continue;
                }
                pathElements.removeLast();
            }
            else {
                pathElements.addLast(token);
            }
        }
        final StringBuffer cleanedPath = new StringBuffer();
        while (!pathElements.isEmpty()) {
            cleanedPath.append(pathElements.removeFirst());
            if (!pathElements.isEmpty()) {
                cleanedPath.append('/');
            }
        }
        return cleanedPath.toString();
    }
}
