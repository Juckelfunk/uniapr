// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.settings;

import org.apache.maven.model.ActivationFile;
import org.apache.maven.model.ActivationOS;
import org.apache.maven.model.ActivationProperty;
import org.apache.maven.model.Activation;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import org.codehaus.plexus.util.StringUtils;
import java.util.List;
import java.util.ArrayList;

public final class SettingsUtils
{
    private SettingsUtils() {
    }
    
    public static void merge(final Settings dominant, final Settings recessive, final String recessiveSourceLevel) {
        if (dominant == null || recessive == null) {
            return;
        }
        recessive.setSourceLevel(recessiveSourceLevel);
        List dominantActiveProfiles = dominant.getActiveProfiles();
        final List recessiveActiveProfiles = recessive.getActiveProfiles();
        if (recessiveActiveProfiles != null) {
            if (dominantActiveProfiles == null) {
                dominantActiveProfiles = new ArrayList();
                dominant.setActiveProfiles(dominantActiveProfiles);
            }
            for (final String profileId : recessiveActiveProfiles) {
                if (!dominantActiveProfiles.contains(profileId)) {
                    dominantActiveProfiles.add(profileId);
                    dominant.getRuntimeInfo().setActiveProfileSourceLevel(profileId, recessiveSourceLevel);
                }
            }
        }
        List dominantPluginGroupIds = dominant.getPluginGroups();
        final List recessivePluginGroupIds = recessive.getPluginGroups();
        if (recessivePluginGroupIds != null) {
            if (dominantPluginGroupIds == null) {
                dominantPluginGroupIds = new ArrayList();
                dominant.setPluginGroups(dominantPluginGroupIds);
            }
            for (final String pluginGroupId : recessivePluginGroupIds) {
                if (!dominantPluginGroupIds.contains(pluginGroupId)) {
                    dominantPluginGroupIds.add(pluginGroupId);
                    dominant.getRuntimeInfo().setPluginGroupIdSourceLevel(pluginGroupId, recessiveSourceLevel);
                }
            }
        }
        if (StringUtils.isEmpty(dominant.getLocalRepository())) {
            dominant.setLocalRepository(recessive.getLocalRepository());
            dominant.getRuntimeInfo().setLocalRepositorySourceLevel(recessiveSourceLevel);
        }
        shallowMergeById(dominant.getMirrors(), recessive.getMirrors(), recessiveSourceLevel);
        shallowMergeById(dominant.getServers(), recessive.getServers(), recessiveSourceLevel);
        shallowMergeById(dominant.getProxies(), recessive.getProxies(), recessiveSourceLevel);
        shallowMergeById(dominant.getProfiles(), recessive.getProfiles(), recessiveSourceLevel);
    }
    
    private static void shallowMergeById(final List dominant, final List recessive, final String recessiveSourceLevel) {
        final Map dominantById = mapById(dominant);
        for (final IdentifiableBase identifiable : recessive) {
            if (!dominantById.containsKey(identifiable.getId())) {
                identifiable.setSourceLevel(recessiveSourceLevel);
                dominant.add(identifiable);
            }
        }
    }
    
    private static Map mapById(final List identifiables) {
        final Map byId = new HashMap();
        for (final IdentifiableBase identifiable : identifiables) {
            byId.put(identifiable.getId(), identifiable);
        }
        return byId;
    }
    
    public static org.apache.maven.model.Profile convertFromSettingsProfile(final Profile settingsProfile) {
        final org.apache.maven.model.Profile profile = new org.apache.maven.model.Profile();
        profile.setId(settingsProfile.getId());
        profile.setSource("settings.xml");
        final org.apache.maven.settings.Activation settingsActivation = settingsProfile.getActivation();
        if (settingsActivation != null) {
            final Activation activation = new Activation();
            activation.setActiveByDefault(settingsActivation.isActiveByDefault());
            activation.setJdk(settingsActivation.getJdk());
            final org.apache.maven.settings.ActivationProperty settingsProp = settingsActivation.getProperty();
            if (settingsProp != null) {
                final ActivationProperty prop = new ActivationProperty();
                prop.setName(settingsProp.getName());
                prop.setValue(settingsProp.getValue());
                activation.setProperty(prop);
            }
            final org.apache.maven.settings.ActivationOS settingsOs = settingsActivation.getOs();
            if (settingsOs != null) {
                final ActivationOS os = new ActivationOS();
                os.setArch(settingsOs.getArch());
                os.setFamily(settingsOs.getFamily());
                os.setName(settingsOs.getName());
                os.setVersion(settingsOs.getVersion());
                activation.setOs(os);
            }
            final org.apache.maven.settings.ActivationFile settingsFile = settingsActivation.getFile();
            if (settingsFile != null) {
                final ActivationFile file = new ActivationFile();
                file.setExists(settingsFile.getExists());
                file.setMissing(settingsFile.getMissing());
                activation.setFile(file);
            }
            profile.setActivation(activation);
        }
        profile.setProperties(settingsProfile.getProperties());
        final List repos = settingsProfile.getRepositories();
        if (repos != null) {
            final Iterator it = repos.iterator();
            while (it.hasNext()) {
                profile.addRepository(convertFromSettingsRepository(it.next()));
            }
        }
        final List pluginRepos = settingsProfile.getPluginRepositories();
        if (pluginRepos != null) {
            final Iterator it2 = pluginRepos.iterator();
            while (it2.hasNext()) {
                profile.addPluginRepository(convertFromSettingsRepository(it2.next()));
            }
        }
        return profile;
    }
    
    private static org.apache.maven.model.Repository convertFromSettingsRepository(final Repository settingsRepo) {
        final org.apache.maven.model.Repository repo = new org.apache.maven.model.Repository();
        repo.setId(settingsRepo.getId());
        repo.setLayout(settingsRepo.getLayout());
        repo.setName(settingsRepo.getName());
        repo.setUrl(settingsRepo.getUrl());
        if (settingsRepo.getSnapshots() != null) {
            repo.setSnapshots(convertRepositoryPolicy(settingsRepo.getSnapshots()));
        }
        if (settingsRepo.getReleases() != null) {
            repo.setReleases(convertRepositoryPolicy(settingsRepo.getReleases()));
        }
        return repo;
    }
    
    private static org.apache.maven.model.RepositoryPolicy convertRepositoryPolicy(final RepositoryPolicy settingsPolicy) {
        final org.apache.maven.model.RepositoryPolicy policy = new org.apache.maven.model.RepositoryPolicy();
        policy.setEnabled(settingsPolicy.isEnabled());
        policy.setUpdatePolicy(settingsPolicy.getUpdatePolicy());
        policy.setChecksumPolicy(settingsPolicy.getChecksumPolicy());
        return policy;
    }
    
    public static Settings copySettings(final Settings settings) {
        if (settings == null) {
            return null;
        }
        final Settings clone = new Settings();
        clone.setActiveProfiles(settings.getActiveProfiles());
        clone.setInteractiveMode(settings.isInteractiveMode());
        clone.setLocalRepository(settings.getLocalRepository());
        clone.setMirrors(settings.getMirrors());
        clone.setModelEncoding(settings.getModelEncoding());
        clone.setOffline(settings.isOffline());
        clone.setPluginGroups(settings.getPluginGroups());
        clone.setProfiles(settings.getProfiles());
        clone.setProxies(settings.getProxies());
        clone.setRuntimeInfo(settings.getRuntimeInfo());
        clone.setServers(settings.getServers());
        clone.setSourceLevel(settings.getSourceLevel());
        clone.setUsePluginRegistry(settings.isUsePluginRegistry());
        return clone;
    }
}
