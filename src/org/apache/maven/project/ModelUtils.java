// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project;

import java.util.LinkedHashMap;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.Resource;
import org.apache.maven.model.Extension;
import org.apache.maven.model.Build;
import java.util.Enumeration;
import java.util.Properties;
import org.apache.maven.model.Notifier;
import org.apache.maven.model.CiManagement;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Relocation;
import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.Site;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.Organization;
import org.apache.maven.model.Prerequisites;
import org.apache.maven.model.Scm;
import org.apache.maven.model.License;
import org.apache.maven.model.Developer;
import org.apache.maven.model.Contributor;
import org.apache.maven.model.Model;
import org.apache.maven.model.PluginExecution;
import java.util.TreeMap;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import java.util.Map;
import java.util.Collection;
import org.apache.maven.model.MailingList;
import org.apache.maven.model.RepositoryPolicy;
import org.apache.maven.model.RepositoryBase;
import org.apache.maven.model.ActivationFile;
import org.apache.maven.model.ActivationOS;
import org.apache.maven.model.ActivationProperty;
import org.apache.maven.model.Activation;
import org.apache.maven.model.Reporting;
import org.apache.maven.model.Repository;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.ModelBase;
import org.apache.maven.model.Profile;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.model.ReportSet;
import java.util.Iterator;
import java.util.List;
import org.apache.maven.model.Plugin;
import java.util.ArrayList;
import org.apache.maven.model.PluginContainer;

public final class ModelUtils
{
    private static final ModelPartCloner DEPENDENCY_CLONER;
    private static final ModelPartCloner PLUGIN_CLONER;
    private static final ModelPartCloner EXTENSION_CLONER;
    private static final ModelPartCloner RESOURCE_CLONER;
    private static final ModelPartCloner NOTIFIER_CLONER;
    private static final ModelPartCloner CONTRIBUTOR_CLONER;
    private static final ModelPartCloner DEVELOPER_CLONER;
    private static final ModelPartCloner LICENSE_CLONER;
    private static final ModelPartCloner MAILING_LIST_CLONER;
    private static final ModelPartCloner REPOSITORY_CLONER;
    private static final ModelPartCloner PROFILE_CLONER;
    private static final ModelPartCloner REPORT_PLUGIN_CLONER;
    private static final ModelPartCloner REPORT_SET_CLONER;
    private static final ModelPartCloner DEPENDENCY_EXCLUSION_CLONER;
    private static final ModelPartCloner PLUGIN_EXECUTION_CLONER;
    
    public static void mergeDuplicatePluginDefinitions(final PluginContainer pluginContainer) {
        if (pluginContainer == null) {
            return;
        }
        final List originalPlugins = pluginContainer.getPlugins();
        if (originalPlugins == null || originalPlugins.isEmpty()) {
            return;
        }
        final List normalized = new ArrayList(originalPlugins.size());
        for (final Plugin currentPlugin : originalPlugins) {
            if (normalized.contains(currentPlugin)) {
                final int idx = normalized.indexOf(currentPlugin);
                final Plugin firstPlugin = normalized.get(idx);
                mergePluginDefinitions(currentPlugin, firstPlugin, false);
                normalized.set(idx, currentPlugin);
            }
            else {
                normalized.add(currentPlugin);
            }
        }
        pluginContainer.setPlugins(normalized);
    }
    
    public static ReportSet cloneReportSet(final ReportSet src) {
        if (src == null) {
            return null;
        }
        final ReportSet result = new ReportSet();
        result.setConfiguration(cloneConfiguration(src.getConfiguration()));
        result.setId(src.getId());
        result.setInherited(src.getInherited());
        result.setReports(cloneListOfStrings(src.getReports()));
        return result;
    }
    
    public static ReportPlugin cloneReportPlugin(final ReportPlugin src) {
        if (src == null) {
            return null;
        }
        final ReportPlugin result = new ReportPlugin();
        result.setArtifactId(src.getArtifactId());
        result.setConfiguration(cloneConfiguration(src.getConfiguration()));
        result.setGroupId(src.getGroupId());
        result.setInherited(src.getInherited());
        result.setReportSets(cloneList(src.getReportSets(), ModelUtils.REPORT_SET_CLONER));
        result.setVersion(src.getVersion());
        return result;
    }
    
    public static Profile cloneProfile(final Profile src) {
        if (src == null) {
            return null;
        }
        final Profile result = new Profile();
        cloneModelBaseFields(src, result);
        result.setActivation(cloneActivation(src.getActivation()));
        BuildBase resultBuild = null;
        if (src.getBuild() != null) {
            resultBuild = new BuildBase();
            cloneBuildBaseFields(src.getBuild(), resultBuild);
        }
        result.setBuild(resultBuild);
        result.setId(src.getId());
        result.setSource(src.getSource());
        return result;
    }
    
    private static void cloneModelBaseFields(final ModelBase src, final ModelBase result) {
        result.setDependencies(cloneList(src.getDependencies(), ModelUtils.DEPENDENCY_CLONER));
        result.setDependencyManagement(cloneDependencyManagement(src.getDependencyManagement()));
        result.setDistributionManagement(cloneDistributionManagement(src.getDistributionManagement()));
        result.setModules(cloneListOfStrings(src.getModules()));
        result.setPluginRepositories(cloneList(src.getPluginRepositories(), ModelUtils.REPOSITORY_CLONER));
        result.setProperties(cloneProperties(src.getProperties()));
        result.setReporting(cloneReporting(src.getReporting()));
        result.setRepositories(cloneList(src.getRepositories(), ModelUtils.REPOSITORY_CLONER));
    }
    
    public static Reporting cloneReporting(final Reporting src) {
        if (src == null) {
            return null;
        }
        final Reporting result = new Reporting();
        result.setExcludeDefaults(src.isExcludeDefaults());
        result.setOutputDirectory(src.getOutputDirectory());
        result.setPlugins(cloneList(src.getPlugins(), ModelUtils.REPORT_PLUGIN_CLONER));
        return result;
    }
    
    public static Activation cloneActivation(final Activation src) {
        if (src == null) {
            return null;
        }
        final Activation result = new Activation();
        result.setActiveByDefault(src.isActiveByDefault());
        result.setFile(cloneActivationFile(src.getFile()));
        result.setJdk(src.getJdk());
        result.setOs(cloneActivationOs(src.getOs()));
        result.setProperty(cloneActivationProperty(src.getProperty()));
        return result;
    }
    
    public static ActivationProperty cloneActivationProperty(final ActivationProperty src) {
        if (src == null) {
            return null;
        }
        final ActivationProperty result = new ActivationProperty();
        result.setName(src.getName());
        result.setValue(src.getValue());
        return result;
    }
    
    public static ActivationOS cloneActivationOs(final ActivationOS src) {
        if (src == null) {
            return null;
        }
        final ActivationOS result = new ActivationOS();
        result.setArch(src.getArch());
        result.setFamily(src.getFamily());
        result.setName(src.getName());
        result.setVersion(src.getVersion());
        return result;
    }
    
    public static ActivationFile cloneActivationFile(final ActivationFile src) {
        if (src == null) {
            return null;
        }
        final ActivationFile result = new ActivationFile();
        result.setExists(src.getExists());
        result.setMissing(src.getMissing());
        return result;
    }
    
    public static Repository cloneRepository(final Repository src) {
        if (src == null) {
            return null;
        }
        final Repository result = new Repository();
        result.setReleases(cloneRepositoryPolicy(src.getReleases()));
        result.setSnapshots(cloneRepositoryPolicy(src.getSnapshots()));
        cloneRepositoryBaseFields(src, result);
        return result;
    }
    
    public static RepositoryPolicy cloneRepositoryPolicy(final RepositoryPolicy src) {
        if (src == null) {
            return null;
        }
        final RepositoryPolicy result = new RepositoryPolicy();
        result.setChecksumPolicy(src.getChecksumPolicy());
        result.setEnabled(src.isEnabled());
        result.setUpdatePolicy(src.getUpdatePolicy());
        return result;
    }
    
    public static MailingList cloneMailingList(final MailingList src) {
        if (src == null) {
            return null;
        }
        final MailingList result = new MailingList();
        result.setArchive(src.getArchive());
        result.setName(src.getName());
        result.setOtherArchives(src.getOtherArchives());
        result.setPost(src.getPost());
        result.setSubscribe(src.getSubscribe());
        result.setUnsubscribe(src.getUnsubscribe());
        return result;
    }
    
    public static void mergePluginLists(final PluginContainer child, final PluginContainer parent, final boolean handleAsInheritance) {
        if (child == null || parent == null) {
            return;
        }
        List parentPlugins = parent.getPlugins();
        if (parentPlugins != null && !parentPlugins.isEmpty()) {
            parentPlugins = new ArrayList(parentPlugins);
            if (handleAsInheritance) {
                final Iterator it = parentPlugins.iterator();
                while (it.hasNext()) {
                    final Plugin plugin = it.next();
                    final String inherited = plugin.getInherited();
                    if (inherited != null && !Boolean.valueOf(inherited)) {
                        it.remove();
                    }
                }
            }
            final List assembledPlugins = new ArrayList();
            final Map childPlugins = child.getPluginsAsMap();
            for (final Plugin parentPlugin : parentPlugins) {
                final String parentInherited = parentPlugin.getInherited();
                if (!handleAsInheritance || parentInherited == null || Boolean.valueOf(parentInherited)) {
                    final Plugin childPlugin = childPlugins.get(parentPlugin.getKey());
                    if (childPlugin != null && !assembledPlugins.contains(childPlugin)) {
                        final Plugin assembledPlugin = childPlugin;
                        mergePluginDefinitions(childPlugin, parentPlugin, handleAsInheritance);
                        assembledPlugins.add(assembledPlugin);
                    }
                    if (handleAsInheritance && parentInherited == null) {
                        parentPlugin.unsetInheritanceApplied();
                    }
                }
                final List results = orderAfterMerge(assembledPlugins, parentPlugins, child.getPlugins());
                child.setPlugins(results);
                child.flushPluginMap();
            }
        }
    }
    
    public static List orderAfterMerge(final List merged, final List highPrioritySource, final List lowPrioritySource) {
        final List results = new ArrayList();
        if (!merged.isEmpty()) {
            results.addAll(merged);
        }
        final List missingFromResults = new ArrayList();
        final List sources = new ArrayList();
        sources.add(highPrioritySource);
        sources.add(lowPrioritySource);
        for (final List source : sources) {
            for (final Object item : source) {
                if (results.contains(item)) {
                    if (missingFromResults.isEmpty()) {
                        continue;
                    }
                    int idx = results.indexOf(item);
                    if (idx < 0) {
                        idx = 0;
                    }
                    results.addAll(idx, missingFromResults);
                    missingFromResults.clear();
                }
                else {
                    missingFromResults.add(item);
                }
            }
            if (!missingFromResults.isEmpty()) {
                results.addAll(missingFromResults);
                missingFromResults.clear();
            }
        }
        return results;
    }
    
    public static void mergeReportPluginLists(final Reporting child, final Reporting parent, final boolean handleAsInheritance) {
        if (child == null || parent == null) {
            return;
        }
        List parentPlugins = parent.getPlugins();
        if (parentPlugins != null && !parentPlugins.isEmpty()) {
            parentPlugins = new ArrayList(parentPlugins);
            if (handleAsInheritance) {
                final Iterator it = parentPlugins.iterator();
                while (it.hasNext()) {
                    final ReportPlugin plugin = it.next();
                    final String inherited = plugin.getInherited();
                    if (inherited != null && !Boolean.valueOf(inherited)) {
                        it.remove();
                    }
                }
            }
            final List assembledPlugins = new ArrayList();
            final Map childPlugins = child.getReportPluginsAsMap();
            for (final ReportPlugin parentPlugin : parentPlugins) {
                final String parentInherited = parentPlugin.getInherited();
                if (!handleAsInheritance || parentInherited == null || Boolean.valueOf(parentInherited)) {
                    final ReportPlugin childPlugin = childPlugins.get(parentPlugin.getKey());
                    if (childPlugin != null && !assembledPlugins.contains(childPlugin)) {
                        final ReportPlugin assembledPlugin = childPlugin;
                        mergeReportPluginDefinitions(childPlugin, parentPlugin, handleAsInheritance);
                        assembledPlugins.add(assembledPlugin);
                    }
                    if (handleAsInheritance && parentInherited == null) {
                        parentPlugin.unsetInheritanceApplied();
                    }
                }
                final List results = orderAfterMerge(assembledPlugins, parentPlugins, child.getPlugins());
                child.setPlugins(results);
                child.flushReportPluginMap();
            }
        }
    }
    
    public static void mergePluginDefinitions(final Plugin child, final Plugin parent, final boolean handleAsInheritance) {
        if (child == null || parent == null) {
            return;
        }
        if (parent.isExtensions()) {
            child.setExtensions(true);
        }
        if (child.getVersion() == null && parent.getVersion() != null) {
            child.setVersion(parent.getVersion());
        }
        Xpp3Dom childConfiguration = (Xpp3Dom)child.getConfiguration();
        final Xpp3Dom parentConfiguration = (Xpp3Dom)parent.getConfiguration();
        childConfiguration = Xpp3Dom.mergeXpp3Dom(childConfiguration, parentConfiguration);
        child.setConfiguration(childConfiguration);
        child.setDependencies(mergeDependencyList(child.getDependencies(), parent.getDependencies()));
        final String parentInherited = parent.getInherited();
        final boolean parentIsInherited = parentInherited == null || Boolean.valueOf(parentInherited);
        final List parentExecutions = parent.getExecutions();
        if (parentExecutions != null && !parentExecutions.isEmpty()) {
            final List mergedExecutions = new ArrayList();
            final Map assembledExecutions = new TreeMap();
            final Map childExecutions = child.getExecutionsAsMap();
            for (final PluginExecution parentExecution : parentExecutions) {
                final String inherited = parentExecution.getInherited();
                final boolean parentExecInherited = parentIsInherited && (inherited == null || Boolean.valueOf(inherited));
                if (!handleAsInheritance || parentExecInherited) {
                    PluginExecution assembled = parentExecution;
                    final PluginExecution childExecution = childExecutions.get(parentExecution.getId());
                    if (childExecution != null) {
                        mergePluginExecutionDefinitions(childExecution, parentExecution);
                        assembled = childExecution;
                    }
                    else if (handleAsInheritance && parentInherited == null) {
                        parentExecution.unsetInheritanceApplied();
                    }
                    assembledExecutions.put(assembled.getId(), assembled);
                    mergedExecutions.add(assembled);
                }
            }
            for (final PluginExecution childExecution2 : child.getExecutions()) {
                if (!assembledExecutions.containsKey(childExecution2.getId())) {
                    mergedExecutions.add(childExecution2);
                }
            }
            child.setExecutions(mergedExecutions);
            child.flushExecutionMap();
        }
    }
    
    public static void mergeReportPluginDefinitions(final ReportPlugin child, final ReportPlugin parent, final boolean handleAsInheritance) {
        if (child == null || parent == null) {
            return;
        }
        if (child.getVersion() == null && parent.getVersion() != null) {
            child.setVersion(parent.getVersion());
        }
        final String parentInherited = parent.getInherited();
        final boolean parentIsInherited = parentInherited == null || Boolean.valueOf(parentInherited);
        if (parentIsInherited) {
            Xpp3Dom childConfiguration = (Xpp3Dom)child.getConfiguration();
            final Xpp3Dom parentConfiguration = (Xpp3Dom)parent.getConfiguration();
            childConfiguration = Xpp3Dom.mergeXpp3Dom(childConfiguration, parentConfiguration);
            child.setConfiguration(childConfiguration);
        }
        final List parentReportSets = parent.getReportSets();
        if (parentReportSets != null && !parentReportSets.isEmpty()) {
            final Map assembledReportSets = new TreeMap();
            final Map childReportSets = child.getReportSetsAsMap();
            for (final ReportSet parentReportSet : parentReportSets) {
                if (!handleAsInheritance || parentIsInherited) {
                    ReportSet assembledReportSet = parentReportSet;
                    final ReportSet childReportSet = childReportSets.get(parentReportSet.getId());
                    if (childReportSet != null) {
                        mergeReportSetDefinitions(childReportSet, parentReportSet);
                        assembledReportSet = childReportSet;
                    }
                    else if (handleAsInheritance && parentInherited == null) {
                        parentReportSet.unsetInheritanceApplied();
                    }
                    assembledReportSets.put(assembledReportSet.getId(), assembledReportSet);
                }
            }
            for (final Map.Entry entry : childReportSets.entrySet()) {
                final String id = entry.getKey();
                if (!assembledReportSets.containsKey(id)) {
                    assembledReportSets.put(id, entry.getValue());
                }
            }
            child.setReportSets(new ArrayList<ReportSet>(assembledReportSets.values()));
            child.flushReportSetMap();
        }
    }
    
    private static void mergePluginExecutionDefinitions(final PluginExecution child, final PluginExecution parent) {
        if (child.getPhase() == null) {
            child.setPhase(parent.getPhase());
        }
        final List parentGoals = parent.getGoals();
        final List childGoals = child.getGoals();
        final List goals = new ArrayList();
        if (childGoals != null && !childGoals.isEmpty()) {
            goals.addAll(childGoals);
        }
        if (parentGoals != null) {
            for (final String goal : parentGoals) {
                if (!goals.contains(goal)) {
                    goals.add(goal);
                }
            }
        }
        child.setGoals(goals);
        Xpp3Dom childConfiguration = (Xpp3Dom)child.getConfiguration();
        final Xpp3Dom parentConfiguration = (Xpp3Dom)parent.getConfiguration();
        childConfiguration = Xpp3Dom.mergeXpp3Dom(childConfiguration, parentConfiguration);
        child.setConfiguration(childConfiguration);
    }
    
    private static void mergeReportSetDefinitions(final ReportSet child, final ReportSet parent) {
        final List parentReports = parent.getReports();
        final List childReports = child.getReports();
        final List reports = new ArrayList();
        if (childReports != null && !childReports.isEmpty()) {
            reports.addAll(childReports);
        }
        if (parentReports != null) {
            for (final String report : parentReports) {
                if (!reports.contains(report)) {
                    reports.add(report);
                }
            }
        }
        child.setReports(reports);
        Xpp3Dom childConfiguration = (Xpp3Dom)child.getConfiguration();
        final Xpp3Dom parentConfiguration = (Xpp3Dom)parent.getConfiguration();
        childConfiguration = Xpp3Dom.mergeXpp3Dom(childConfiguration, parentConfiguration);
        child.setConfiguration(childConfiguration);
    }
    
    public static Model cloneModel(final Model src) {
        if (src == null) {
            return null;
        }
        final Model result = new Model();
        cloneModelBaseFields(src, result);
        result.setArtifactId(src.getArtifactId());
        result.setBuild(cloneBuild(src.getBuild()));
        result.setCiManagement(cloneCiManagement(src.getCiManagement()));
        result.setContributors(cloneList(src.getContributors(), ModelUtils.CONTRIBUTOR_CLONER));
        result.setDescription(src.getDescription());
        result.setDevelopers(cloneList(src.getDevelopers(), ModelUtils.DEVELOPER_CLONER));
        result.setGroupId(src.getGroupId());
        result.setInceptionYear(src.getInceptionYear());
        result.setIssueManagement(cloneIssueManagement(src.getIssueManagement()));
        result.setLicenses(cloneList(src.getLicenses(), ModelUtils.LICENSE_CLONER));
        result.setMailingLists(cloneList(src.getMailingLists(), ModelUtils.MAILING_LIST_CLONER));
        result.setModelVersion(src.getModelVersion());
        result.setName(src.getName());
        result.setOrganization(cloneOrganization(src.getOrganization()));
        result.setPackaging(src.getPackaging());
        result.setParent(cloneParent(src.getParent()));
        result.setPrerequisites(clonePrerequisites(src.getPrerequisites()));
        result.setProfiles(cloneList(src.getProfiles(), ModelUtils.PROFILE_CLONER));
        result.setScm(cloneScm(src.getScm()));
        result.setUrl(src.getUrl());
        result.setVersion(src.getVersion());
        return result;
    }
    
    public static Scm cloneScm(final Scm src) {
        if (src == null) {
            return null;
        }
        final Scm result = new Scm();
        result.setConnection(src.getConnection());
        result.setDeveloperConnection(src.getDeveloperConnection());
        result.setTag(src.getTag());
        result.setUrl(src.getUrl());
        return result;
    }
    
    public static Prerequisites clonePrerequisites(final Prerequisites src) {
        if (src == null) {
            return null;
        }
        final Prerequisites result = new Prerequisites();
        result.setMaven(src.getMaven());
        return result;
    }
    
    public static Organization cloneOrganization(final Organization src) {
        if (src == null) {
            return null;
        }
        final Organization result = new Organization();
        result.setName(src.getName());
        result.setUrl(src.getUrl());
        return result;
    }
    
    public static License cloneLicense(final License src) {
        if (src == null) {
            return null;
        }
        final License result = new License();
        result.setComments(src.getComments());
        result.setDistribution(src.getDistribution());
        result.setName(src.getName());
        result.setUrl(src.getUrl());
        return result;
    }
    
    public static IssueManagement cloneIssueManagement(final IssueManagement src) {
        if (src == null) {
            return null;
        }
        final IssueManagement result = new IssueManagement();
        result.setSystem(src.getSystem());
        result.setUrl(src.getUrl());
        return result;
    }
    
    public static DistributionManagement cloneDistributionManagement(final DistributionManagement src) {
        if (src == null) {
            return null;
        }
        final DistributionManagement result = new DistributionManagement();
        result.setDownloadUrl(src.getDownloadUrl());
        result.setRelocation(cloneRelocation(src.getRelocation()));
        result.setRepository(cloneDeploymentRepository(src.getRepository()));
        result.setSite(cloneSite(src.getSite()));
        result.setSnapshotRepository(cloneDeploymentRepository(src.getSnapshotRepository()));
        result.setStatus(src.getStatus());
        return result;
    }
    
    public static Site cloneSite(final Site src) {
        if (src == null) {
            return null;
        }
        final Site result = new Site();
        result.setId(src.getId());
        result.setName(src.getName());
        result.setUrl(src.getUrl());
        return result;
    }
    
    public static DeploymentRepository cloneDeploymentRepository(final DeploymentRepository src) {
        if (src == null) {
            return null;
        }
        final DeploymentRepository result = new DeploymentRepository();
        result.setUniqueVersion(src.isUniqueVersion());
        cloneRepositoryBaseFields(src, result);
        return result;
    }
    
    private static void cloneRepositoryBaseFields(final RepositoryBase src, final RepositoryBase result) {
        result.setId(src.getId());
        result.setLayout(src.getLayout());
        result.setName(src.getName());
        result.setUrl(src.getUrl());
    }
    
    public static Relocation cloneRelocation(final Relocation src) {
        if (src == null) {
            return null;
        }
        final Relocation result = new Relocation();
        result.setArtifactId(src.getArtifactId());
        result.setGroupId(src.getGroupId());
        result.setMessage(src.getMessage());
        result.setVersion(src.getVersion());
        return result;
    }
    
    public static DependencyManagement cloneDependencyManagement(final DependencyManagement src) {
        if (src == null) {
            return null;
        }
        final DependencyManagement result = new DependencyManagement();
        result.setDependencies(cloneList(src.getDependencies(), ModelUtils.DEPENDENCY_CLONER));
        return result;
    }
    
    private static List cloneList(final List src, final ModelPartCloner cloner) {
        List result = null;
        if (src != null) {
            result = new ArrayList(src.size());
            final Iterator it = src.iterator();
            while (it.hasNext()) {
                result.add(cloner.cloneModelPart(it.next()));
            }
        }
        return result;
    }
    
    public static Contributor cloneContributor(final Contributor src) {
        if (src == null) {
            return null;
        }
        final Contributor result = new Contributor();
        cloneContributorFields(src, result);
        return result;
    }
    
    public static Developer cloneDeveloper(final Developer src) {
        if (src == null) {
            return null;
        }
        final Developer result = new Developer();
        result.setId(src.getId());
        cloneContributorFields(src, result);
        return result;
    }
    
    private static void cloneContributorFields(final Contributor src, final Contributor result) {
        result.setEmail(src.getEmail());
        result.setName(src.getName());
        result.setOrganization(src.getOrganization());
        result.setOrganizationUrl(src.getOrganizationUrl());
        result.setProperties(cloneProperties(src.getProperties()));
        result.setRoles(cloneListOfStrings(src.getRoles()));
        result.setTimezone(src.getTimezone());
        result.setUrl(src.getUrl());
    }
    
    public static CiManagement cloneCiManagement(final CiManagement src) {
        if (src == null) {
            return null;
        }
        final CiManagement result = new CiManagement();
        List notifiers = null;
        if (src.getNotifiers() != null) {
            notifiers = new ArrayList(src.getNotifiers().size());
            final Iterator it = src.getNotifiers().iterator();
            while (it.hasNext()) {
                notifiers.add(cloneNotifier(it.next()));
            }
        }
        result.setNotifiers(cloneList(src.getNotifiers(), ModelUtils.NOTIFIER_CLONER));
        result.setSystem(src.getSystem());
        result.setUrl(src.getUrl());
        return result;
    }
    
    public static Notifier cloneNotifier(final Notifier src) {
        if (src == null) {
            return null;
        }
        final Notifier result = new Notifier();
        result.setAddress(src.getAddress());
        result.setConfiguration(cloneProperties(src.getConfiguration()));
        result.setSendOnError(src.isSendOnError());
        result.setSendOnFailure(result.isSendOnFailure());
        result.setSendOnSuccess(result.isSendOnSuccess());
        result.setSendOnWarning(result.isSendOnWarning());
        return result;
    }
    
    public static Properties cloneProperties(final Properties src) {
        if (src == null) {
            return null;
        }
        final Properties result = new Properties();
        final Enumeration e = src.propertyNames();
        while (e.hasMoreElements()) {
            final String key = e.nextElement();
            result.setProperty(key, src.getProperty(key));
        }
        return result;
    }
    
    public static Build cloneBuild(final Build src) {
        if (src == null) {
            return null;
        }
        final Build result = new Build();
        cloneBuildBaseFields(src, result);
        result.setExtensions(cloneList(src.getExtensions(), ModelUtils.EXTENSION_CLONER));
        result.setOutputDirectory(src.getOutputDirectory());
        result.setScriptSourceDirectory(src.getScriptSourceDirectory());
        result.setSourceDirectory(src.getSourceDirectory());
        result.setTestOutputDirectory(src.getTestOutputDirectory());
        result.setTestSourceDirectory(src.getTestSourceDirectory());
        return result;
    }
    
    public static void cloneBuildBaseFields(final BuildBase src, final BuildBase result) {
        result.setDefaultGoal(src.getDefaultGoal());
        result.setDirectory(src.getDirectory());
        result.setFilters(cloneListOfStrings(src.getFilters()));
        result.setFinalName(src.getFinalName());
        result.setPluginManagement(clonePluginManagement(src.getPluginManagement()));
        result.setPlugins(cloneList(src.getPlugins(), ModelUtils.PLUGIN_CLONER));
        result.setResources(cloneList(src.getResources(), ModelUtils.RESOURCE_CLONER));
        result.setTestResources(cloneList(src.getTestResources(), ModelUtils.RESOURCE_CLONER));
    }
    
    public static PluginManagement clonePluginManagement(final PluginManagement src) {
        PluginManagement pMgmt = null;
        if (src != null) {
            pMgmt = new PluginManagement();
            pMgmt.setPlugins(cloneList(src.getPlugins(), ModelUtils.PLUGIN_CLONER));
        }
        return pMgmt;
    }
    
    public static Resource cloneResource(final Resource src) {
        Resource result = null;
        if (src != null) {
            result = new Resource();
            result.setDirectory(src.getDirectory());
            result.setExcludes(cloneListOfStrings(src.getExcludes()));
            result.setFiltering(src.isFiltering());
            result.setIncludes(cloneListOfStrings(src.getIncludes()));
            result.setMergeId(src.getMergeId());
            result.setTargetPath(src.getTargetPath());
        }
        return result;
    }
    
    public static Plugin clonePlugin(final Plugin src) {
        Plugin result = null;
        if (src != null) {
            result = new Plugin();
            result.setArtifactId(src.getArtifactId());
            result.setConfiguration(cloneConfiguration(src.getConfiguration()));
            result.setDependencies(cloneList(src.getDependencies(), ModelUtils.DEPENDENCY_CLONER));
            result.setExecutions(cloneList(src.getExecutions(), ModelUtils.PLUGIN_EXECUTION_CLONER));
            result.setExtensions(src.isExtensions());
            result.setGroupId(src.getGroupId());
            result.setInherited(src.getInherited());
            result.setVersion(src.getVersion());
        }
        return result;
    }
    
    public static PluginExecution clonePluginExecution(final PluginExecution src) {
        PluginExecution result = null;
        if (src != null) {
            result = new PluginExecution();
            result.setId(src.getId());
            result.setGoals(cloneListOfStrings(src.getGoals()));
            result.setConfiguration(cloneConfiguration(src.getConfiguration()));
            result.setInherited(src.getInherited());
            result.setPhase(src.getPhase());
        }
        return result;
    }
    
    public static Object cloneConfiguration(final Object configuration) {
        if (configuration == null) {
            return null;
        }
        return new Xpp3Dom((Xpp3Dom)configuration);
    }
    
    public static Dependency cloneDependency(final Dependency src) {
        Dependency result = null;
        if (src != null) {
            result = new Dependency();
            result.setArtifactId(src.getArtifactId());
            result.setClassifier(src.getClassifier());
            result.setExclusions(cloneList(src.getExclusions(), ModelUtils.DEPENDENCY_EXCLUSION_CLONER));
            result.setGroupId(src.getGroupId());
            result.setOptional(src.isOptional());
            result.setScope(src.getScope());
            result.setSystemPath(src.getSystemPath());
            result.setType(src.getType());
            result.setVersion(src.getVersion());
        }
        return result;
    }
    
    public static Exclusion cloneExclusion(final Exclusion src) {
        Exclusion result = null;
        if (src != null) {
            result = new Exclusion();
            result.setArtifactId(src.getArtifactId());
            result.setGroupId(src.getGroupId());
        }
        return result;
    }
    
    public static List cloneListOfStrings(final List src) {
        List result = null;
        if (src != null) {
            result = new ArrayList(src.size());
            for (final String item : src) {
                result.add(item);
            }
        }
        return result;
    }
    
    public static Extension cloneExtension(final Extension src) {
        final Extension rExt = new Extension();
        rExt.setArtifactId(src.getArtifactId());
        rExt.setGroupId(src.getGroupId());
        rExt.setVersion(src.getVersion());
        return rExt;
    }
    
    public static Exclusion cloneDependencyExclusion(final Exclusion src) {
        if (src == null) {
            return null;
        }
        final Exclusion result = new Exclusion();
        result.setArtifactId(src.getArtifactId());
        result.setGroupId(src.getGroupId());
        return result;
    }
    
    public static Parent cloneParent(final Parent src) {
        if (src == null) {
            return null;
        }
        final Parent result = new Parent();
        result.setArtifactId(src.getArtifactId());
        result.setGroupId(src.getGroupId());
        result.setRelativePath(src.getRelativePath());
        result.setVersion(src.getVersion());
        return result;
    }
    
    public static List mergeRepositoryLists(final List dominant, final List recessive) {
        final List repositories = new ArrayList();
        for (final Repository repository : dominant) {
            repositories.add(repository);
        }
        for (final Repository repository : recessive) {
            if (!repositories.contains(repository)) {
                repositories.add(repository);
            }
        }
        return repositories;
    }
    
    public static void mergeExtensionLists(final Build childBuild, final Build parentBuild) {
        final Map extMap = new LinkedHashMap();
        List ext = childBuild.getExtensions();
        if (ext != null) {
            for (final Extension extension : ext) {
                extMap.put(extension.getKey(), extension);
            }
        }
        ext = parentBuild.getExtensions();
        if (ext != null) {
            for (final Extension extension : ext) {
                if (!extMap.containsKey(extension.getKey())) {
                    extMap.put(extension.getKey(), extension);
                }
            }
        }
        childBuild.setExtensions(new ArrayList<Extension>(extMap.values()));
    }
    
    public static void mergeResourceLists(final List childResources, final List parentResources) {
        for (final Resource r : parentResources) {
            if (!childResources.contains(r)) {
                childResources.add(r);
            }
        }
    }
    
    public static void mergeFilterLists(final List childFilters, final List parentFilters) {
        for (final String f : parentFilters) {
            if (!childFilters.contains(f)) {
                childFilters.add(f);
            }
        }
    }
    
    public static List mergeDependencyList(final List child, final List parent) {
        final Map depsMap = new LinkedHashMap();
        if (child != null) {
            for (final Dependency dependency : child) {
                depsMap.put(dependency.getManagementKey(), dependency);
            }
        }
        if (parent != null) {
            for (final Dependency dependency : parent) {
                if (!depsMap.containsKey(dependency.getManagementKey())) {
                    depsMap.put(dependency.getManagementKey(), dependency);
                }
            }
        }
        return new ArrayList(depsMap.values());
    }
    
    static {
        DEPENDENCY_CLONER = new ModelPartCloner() {
            public Object cloneModelPart(final Object src) {
                return ModelUtils.cloneDependency((Dependency)src);
            }
        };
        PLUGIN_CLONER = new ModelPartCloner() {
            public Object cloneModelPart(final Object src) {
                return ModelUtils.clonePlugin((Plugin)src);
            }
        };
        EXTENSION_CLONER = new ModelPartCloner() {
            public Object cloneModelPart(final Object src) {
                return ModelUtils.cloneExtension((Extension)src);
            }
        };
        RESOURCE_CLONER = new ModelPartCloner() {
            public Object cloneModelPart(final Object src) {
                return ModelUtils.cloneResource((Resource)src);
            }
        };
        NOTIFIER_CLONER = new ModelPartCloner() {
            public Object cloneModelPart(final Object src) {
                return ModelUtils.cloneNotifier((Notifier)src);
            }
        };
        CONTRIBUTOR_CLONER = new ModelPartCloner() {
            public Object cloneModelPart(final Object src) {
                return ModelUtils.cloneContributor((Contributor)src);
            }
        };
        DEVELOPER_CLONER = new ModelPartCloner() {
            public Object cloneModelPart(final Object src) {
                return ModelUtils.cloneDeveloper((Developer)src);
            }
        };
        LICENSE_CLONER = new ModelPartCloner() {
            public Object cloneModelPart(final Object src) {
                return ModelUtils.cloneLicense((License)src);
            }
        };
        MAILING_LIST_CLONER = new ModelPartCloner() {
            public Object cloneModelPart(final Object src) {
                return ModelUtils.cloneMailingList((MailingList)src);
            }
        };
        REPOSITORY_CLONER = new ModelPartCloner() {
            public Object cloneModelPart(final Object src) {
                return ModelUtils.cloneRepository((Repository)src);
            }
        };
        PROFILE_CLONER = new ModelPartCloner() {
            public Object cloneModelPart(final Object src) {
                return ModelUtils.cloneProfile((Profile)src);
            }
        };
        REPORT_PLUGIN_CLONER = new ModelPartCloner() {
            public Object cloneModelPart(final Object src) {
                return ModelUtils.cloneReportPlugin((ReportPlugin)src);
            }
        };
        REPORT_SET_CLONER = new ModelPartCloner() {
            public Object cloneModelPart(final Object src) {
                return ModelUtils.cloneReportSet((ReportSet)src);
            }
        };
        DEPENDENCY_EXCLUSION_CLONER = new ModelPartCloner() {
            public Object cloneModelPart(final Object src) {
                return ModelUtils.cloneExclusion((Exclusion)src);
            }
        };
        PLUGIN_EXECUTION_CLONER = new ModelPartCloner() {
            public Object cloneModelPart(final Object src) {
                return ModelUtils.clonePluginExecution((PluginExecution)src);
            }
        };
    }
    
    public interface ModelPartCloner
    {
        Object cloneModelPart(final Object p0);
    }
}
