// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project.injection;

import org.apache.maven.model.ReportSet;
import org.apache.maven.model.Reporting;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.DistributionManagement;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import java.util.Collection;
import org.apache.maven.model.PluginExecution;
import java.util.LinkedHashMap;
import org.apache.maven.model.ConfigurationContainer;
import java.util.Iterator;
import org.apache.maven.model.Plugin;
import java.util.ArrayList;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.PluginContainer;
import org.apache.maven.model.Build;
import java.util.Map;
import java.util.Properties;
import org.apache.maven.model.Repository;
import org.apache.maven.project.ModelUtils;
import org.apache.maven.model.Dependency;
import java.util.List;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;

public class DefaultProfileInjector implements ProfileInjector
{
    public void inject(final Profile profile, final Model model) {
        model.setDependencies(this.injectDependencies(profile.getDependencies(), model.getDependencies()));
        this.injectModules(profile, model);
        model.setRepositories(ModelUtils.mergeRepositoryLists(profile.getRepositories(), model.getRepositories()));
        model.setPluginRepositories(ModelUtils.mergeRepositoryLists(profile.getPluginRepositories(), model.getPluginRepositories()));
        this.injectReporting(profile, model);
        this.injectDependencyManagement(profile, model);
        this.injectDistributionManagement(profile, model);
        this.injectBuild(profile, model);
        final Properties props = new Properties();
        props.putAll(model.getProperties());
        props.putAll(profile.getProperties());
        model.setProperties(props);
    }
    
    private void injectBuild(final Profile profile, final Model model) {
        final BuildBase profileBuild = profile.getBuild();
        Build modelBuild = model.getBuild();
        if (profileBuild != null) {
            if (modelBuild == null) {
                modelBuild = new Build();
                model.setBuild(modelBuild);
            }
            if (profileBuild.getDirectory() != null) {
                modelBuild.setDirectory(profileBuild.getDirectory());
            }
            if (profileBuild.getDefaultGoal() != null) {
                modelBuild.setDefaultGoal(profileBuild.getDefaultGoal());
            }
            if (profileBuild.getFinalName() != null) {
                modelBuild.setFinalName(profileBuild.getFinalName());
            }
            ModelUtils.mergeFilterLists(modelBuild.getFilters(), profileBuild.getFilters());
            ModelUtils.mergeResourceLists(modelBuild.getResources(), profileBuild.getResources());
            ModelUtils.mergeResourceLists(modelBuild.getTestResources(), profileBuild.getTestResources());
            this.injectPlugins(profileBuild, modelBuild);
            final PluginManagement profilePM = profileBuild.getPluginManagement();
            final PluginManagement modelPM = modelBuild.getPluginManagement();
            if (modelPM == null) {
                modelBuild.setPluginManagement(profilePM);
            }
            else {
                this.injectPlugins(profilePM, modelPM);
            }
        }
    }
    
    protected void injectPlugins(final PluginContainer profileContainer, final PluginContainer modelContainer) {
        if (profileContainer == null || modelContainer == null) {
            return;
        }
        final List modelPlugins = modelContainer.getPlugins();
        if (modelPlugins == null) {
            modelContainer.setPlugins(profileContainer.getPlugins());
        }
        else if (profileContainer.getPlugins() != null) {
            final List mergedPlugins = new ArrayList();
            final Map profilePlugins = profileContainer.getPluginsAsMap();
            for (final Plugin modelPlugin : modelPlugins) {
                final Plugin profilePlugin = profilePlugins.get(modelPlugin.getKey());
                if (profilePlugin != null && !mergedPlugins.contains(profilePlugin)) {
                    final Plugin mergedPlugin = modelPlugin;
                    this.injectPluginDefinition(profilePlugin, modelPlugin);
                    mergedPlugins.add(mergedPlugin);
                }
            }
            final List results = ModelUtils.orderAfterMerge(mergedPlugins, modelPlugins, profileContainer.getPlugins());
            modelContainer.setPlugins(results);
            modelContainer.flushPluginMap();
        }
    }
    
    private void injectPluginDefinition(final Plugin profilePlugin, final Plugin modelPlugin) {
        if (profilePlugin == null || modelPlugin == null) {
            return;
        }
        if (profilePlugin.isExtensions()) {
            modelPlugin.setExtensions(true);
        }
        if (profilePlugin.getVersion() != null) {
            modelPlugin.setVersion(profilePlugin.getVersion());
        }
        modelPlugin.setDependencies(this.injectDependencies(profilePlugin.getDependencies(), modelPlugin.getDependencies()));
        this.injectConfigurationContainer(profilePlugin, modelPlugin);
        final List modelExecutions = modelPlugin.getExecutions();
        if (modelExecutions == null || modelExecutions.isEmpty()) {
            modelPlugin.setExecutions(profilePlugin.getExecutions());
        }
        else {
            final Map executions = new LinkedHashMap();
            final Map profileExecutions = profilePlugin.getExecutionsAsMap();
            for (final PluginExecution modelExecution : modelExecutions) {
                final PluginExecution profileExecution = profileExecutions.get(modelExecution.getId());
                if (profileExecution != null) {
                    this.injectConfigurationContainer(profileExecution, modelExecution);
                    if (profileExecution.getPhase() != null) {
                        modelExecution.setPhase(profileExecution.getPhase());
                    }
                    final List profileGoals = profileExecution.getGoals();
                    final List modelGoals = modelExecution.getGoals();
                    final List goals = new ArrayList();
                    if (modelGoals != null && !modelGoals.isEmpty()) {
                        goals.addAll(modelGoals);
                    }
                    if (profileGoals != null) {
                        for (final String goal : profileGoals) {
                            if (!goals.contains(goal)) {
                                goals.add(goal);
                            }
                        }
                    }
                    modelExecution.setGoals(goals);
                }
                executions.put(modelExecution.getId(), modelExecution);
            }
            for (final Map.Entry entry : profileExecutions.entrySet()) {
                final String id = entry.getKey();
                if (!executions.containsKey(id)) {
                    executions.put(id, entry.getValue());
                }
            }
            modelPlugin.setExecutions(new ArrayList<PluginExecution>(executions.values()));
            modelPlugin.flushExecutionMap();
        }
    }
    
    private Xpp3Dom merge(final Xpp3Dom dominant, final Xpp3Dom recessive) {
        final Xpp3Dom dominantCopy = (dominant == null) ? null : new Xpp3Dom(dominant);
        return Xpp3Dom.mergeXpp3Dom(dominantCopy, recessive);
    }
    
    private void injectConfigurationContainer(final ConfigurationContainer profileContainer, final ConfigurationContainer modelContainer) {
        Xpp3Dom configuration = (Xpp3Dom)profileContainer.getConfiguration();
        final Xpp3Dom parentConfiguration = (Xpp3Dom)modelContainer.getConfiguration();
        configuration = this.merge(configuration, parentConfiguration);
        modelContainer.setConfiguration(configuration);
    }
    
    private void injectModules(final Profile profile, final Model model) {
        final List modules = new ArrayList();
        final List modelModules = model.getModules();
        if (modelModules != null && !modelModules.isEmpty()) {
            modules.addAll(modelModules);
        }
        final List profileModules = profile.getModules();
        if (profileModules != null) {
            for (final String module : profileModules) {
                if (!modules.contains(module)) {
                    modules.add(module);
                }
            }
        }
        model.setModules(modules);
    }
    
    private void injectDistributionManagement(final Profile profile, final Model model) {
        final DistributionManagement pDistMgmt = profile.getDistributionManagement();
        final DistributionManagement mDistMgmt = model.getDistributionManagement();
        if (mDistMgmt == null) {
            model.setDistributionManagement(pDistMgmt);
        }
        else if (pDistMgmt != null) {
            if (pDistMgmt.getRepository() != null) {
                mDistMgmt.setRepository(pDistMgmt.getRepository());
            }
            if (pDistMgmt.getSnapshotRepository() != null) {
                mDistMgmt.setSnapshotRepository(pDistMgmt.getSnapshotRepository());
            }
            if (StringUtils.isNotEmpty(pDistMgmt.getDownloadUrl())) {
                mDistMgmt.setDownloadUrl(pDistMgmt.getDownloadUrl());
            }
            if (pDistMgmt.getRelocation() != null) {
                mDistMgmt.setRelocation(pDistMgmt.getRelocation());
            }
            if (pDistMgmt.getSite() != null) {
                mDistMgmt.setSite(pDistMgmt.getSite());
            }
        }
    }
    
    private void injectDependencyManagement(final Profile profile, final Model model) {
        final DependencyManagement modelDepMgmt = model.getDependencyManagement();
        final DependencyManagement profileDepMgmt = profile.getDependencyManagement();
        if (profileDepMgmt != null) {
            if (modelDepMgmt == null) {
                model.setDependencyManagement(profileDepMgmt);
            }
            else {
                final Map depsMap = new LinkedHashMap();
                List deps = modelDepMgmt.getDependencies();
                if (deps != null) {
                    for (final Dependency dependency : deps) {
                        depsMap.put(dependency.getManagementKey(), dependency);
                    }
                }
                deps = profileDepMgmt.getDependencies();
                if (deps != null) {
                    for (final Dependency dependency : deps) {
                        depsMap.put(dependency.getManagementKey(), dependency);
                    }
                }
                modelDepMgmt.setDependencies(new ArrayList<Dependency>(depsMap.values()));
            }
        }
    }
    
    private void injectReporting(final Profile profile, final Model model) {
        final Reporting profileReporting = profile.getReporting();
        final Reporting modelReporting = model.getReporting();
        if (profileReporting != null) {
            if (modelReporting == null) {
                model.setReporting(profileReporting);
            }
            else {
                if (StringUtils.isEmpty(modelReporting.getOutputDirectory())) {
                    modelReporting.setOutputDirectory(profileReporting.getOutputDirectory());
                }
                final Map mergedReportPlugins = new LinkedHashMap();
                final Map profileReportersByKey = profileReporting.getReportPluginsAsMap();
                final List modelReportPlugins = modelReporting.getPlugins();
                if (modelReportPlugins != null) {
                    for (final ReportPlugin modelReportPlugin : modelReportPlugins) {
                        final String inherited = modelReportPlugin.getInherited();
                        if (StringUtils.isEmpty(inherited) || Boolean.valueOf(inherited)) {
                            final ReportPlugin profileReportPlugin = profileReportersByKey.get(modelReportPlugin.getKey());
                            ReportPlugin mergedReportPlugin = modelReportPlugin;
                            if (profileReportPlugin != null) {
                                mergedReportPlugin = profileReportPlugin;
                                this.mergeReportPlugins(profileReportPlugin, modelReportPlugin);
                            }
                            else if (StringUtils.isEmpty(inherited)) {
                                mergedReportPlugin.unsetInheritanceApplied();
                            }
                            mergedReportPlugins.put(mergedReportPlugin.getKey(), mergedReportPlugin);
                        }
                    }
                }
                for (final Map.Entry entry : profileReportersByKey.entrySet()) {
                    final String key = entry.getKey();
                    if (!mergedReportPlugins.containsKey(key)) {
                        mergedReportPlugins.put(key, entry.getValue());
                    }
                }
                modelReporting.setPlugins(new ArrayList<ReportPlugin>(mergedReportPlugins.values()));
                modelReporting.flushReportPluginMap();
            }
        }
    }
    
    private void mergeReportPlugins(final ReportPlugin dominant, final ReportPlugin recessive) {
        if (StringUtils.isEmpty(recessive.getVersion())) {
            recessive.setVersion(dominant.getVersion());
        }
        final Xpp3Dom dominantConfig = (Xpp3Dom)dominant.getConfiguration();
        final Xpp3Dom recessiveConfig = (Xpp3Dom)recessive.getConfiguration();
        recessive.setConfiguration(this.merge(dominantConfig, recessiveConfig));
        final Map mergedReportSets = new LinkedHashMap();
        final Map dominantReportSetsById = dominant.getReportSetsAsMap();
        for (final ReportSet recessiveReportSet : recessive.getReportSets()) {
            final ReportSet dominantReportSet = dominantReportSetsById.get(recessiveReportSet.getId());
            ReportSet merged = recessiveReportSet;
            if (dominantReportSet != null) {
                merged = recessiveReportSet;
                final Xpp3Dom dominantRSConfig = (Xpp3Dom)dominantReportSet.getConfiguration();
                final Xpp3Dom mergedRSConfig = (Xpp3Dom)merged.getConfiguration();
                merged.setConfiguration(this.merge(dominantRSConfig, mergedRSConfig));
                List mergedReports = merged.getReports();
                if (mergedReports == null) {
                    mergedReports = new ArrayList();
                    merged.setReports(mergedReports);
                }
                final List dominantRSReports = dominantReportSet.getReports();
                if (dominantRSReports != null) {
                    for (final String report : dominantRSReports) {
                        if (!mergedReports.contains(report)) {
                            mergedReports.add(report);
                        }
                    }
                }
                mergedReportSets.put(merged.getId(), merged);
            }
        }
        for (final Map.Entry entry : dominantReportSetsById.entrySet()) {
            final String key = entry.getKey();
            if (!mergedReportSets.containsKey(key)) {
                mergedReportSets.put(key, entry.getValue());
            }
        }
        recessive.setReportSets(new ArrayList<ReportSet>(mergedReportSets.values()));
        recessive.flushReportSetMap();
    }
    
    private List injectDependencies(final List profileDeps, final List modelDeps) {
        final Map depsMap = new LinkedHashMap();
        if (modelDeps != null) {
            for (final Dependency dependency : modelDeps) {
                depsMap.put(dependency.getManagementKey(), dependency);
            }
        }
        if (profileDeps != null) {
            for (final Dependency dependency : profileDeps) {
                depsMap.put(dependency.getManagementKey(), dependency);
            }
        }
        return new ArrayList(depsMap.values());
    }
}
