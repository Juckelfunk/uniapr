// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.profiles;

import org.apache.maven.settings.SettingsUtils;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.apache.maven.profiles.activation.ProfileActivator;
import org.apache.maven.profiles.activation.ProfileActivationException;
import java.util.Collection;
import java.util.Iterator;
import org.apache.maven.model.Activation;
import org.apache.maven.model.Profile;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import org.apache.maven.settings.Settings;
import java.util.Properties;
import java.util.Map;
import java.util.List;
import org.codehaus.plexus.PlexusContainer;

public class DefaultProfileManager implements ProfileManager
{
    private PlexusContainer container;
    private List activatedIds;
    private List deactivatedIds;
    private List defaultIds;
    private Map profilesById;
    private Properties requestProperties;
    
    @Deprecated
    public DefaultProfileManager(final PlexusContainer container) {
        this(container, (Settings)null);
    }
    
    public DefaultProfileManager(final PlexusContainer container, final Properties props) {
        this(container, null, props);
    }
    
    @Deprecated
    public DefaultProfileManager(final PlexusContainer container, final Settings settings) {
        this.activatedIds = new ArrayList();
        this.deactivatedIds = new ArrayList();
        this.defaultIds = new ArrayList();
        this.profilesById = new LinkedHashMap();
        this.container = container;
        this.loadSettingsProfiles(settings);
    }
    
    public DefaultProfileManager(final PlexusContainer container, final Settings settings, final Properties props) {
        this.activatedIds = new ArrayList();
        this.deactivatedIds = new ArrayList();
        this.defaultIds = new ArrayList();
        this.profilesById = new LinkedHashMap();
        this.container = container;
        this.loadSettingsProfiles(settings);
        if (props != null) {
            this.requestProperties = props;
        }
    }
    
    public Properties getRequestProperties() {
        return this.requestProperties;
    }
    
    public Map getProfilesById() {
        return this.profilesById;
    }
    
    public void addProfile(final Profile profile) {
        final String profileId = profile.getId();
        final Profile existing = this.profilesById.get(profileId);
        if (existing != null) {
            this.container.getLogger().warn("Overriding profile: '" + profileId + "' (source: " + existing.getSource() + ") with new instance from source: " + profile.getSource());
        }
        this.profilesById.put(profile.getId(), profile);
        final Activation activation = profile.getActivation();
        if (activation != null && activation.isActiveByDefault()) {
            this.activateAsDefault(profileId);
        }
    }
    
    public void explicitlyActivate(final String profileId) {
        if (!this.activatedIds.contains(profileId)) {
            this.container.getLogger().debug("Profile with id: '" + profileId + "' has been explicitly activated.");
            this.activatedIds.add(profileId);
        }
    }
    
    public void explicitlyActivate(final List profileIds) {
        for (final String profileId : profileIds) {
            this.explicitlyActivate(profileId);
        }
    }
    
    public void explicitlyDeactivate(final String profileId) {
        if (!this.deactivatedIds.contains(profileId)) {
            this.container.getLogger().debug("Profile with id: '" + profileId + "' has been explicitly deactivated.");
            this.deactivatedIds.add(profileId);
        }
    }
    
    public void explicitlyDeactivate(final List profileIds) {
        for (final String profileId : profileIds) {
            this.explicitlyDeactivate(profileId);
        }
    }
    
    public List getActiveProfiles() throws ProfileActivationException {
        final List activeFromPom = new ArrayList();
        final List activeExternal = new ArrayList();
        for (final Map.Entry entry : this.profilesById.entrySet()) {
            final String profileId = entry.getKey();
            final Profile profile = entry.getValue();
            boolean shouldAdd = false;
            if (this.activatedIds.contains(profileId)) {
                shouldAdd = true;
            }
            else if (this.isActive(profile)) {
                shouldAdd = true;
            }
            if (!this.deactivatedIds.contains(profileId) && shouldAdd) {
                if ("pom".equals(profile.getSource())) {
                    activeFromPom.add(profile);
                }
                else {
                    activeExternal.add(profile);
                }
            }
        }
        if (activeFromPom.isEmpty()) {
            for (final String profileId2 : this.defaultIds) {
                if (this.deactivatedIds.contains(profileId2)) {
                    continue;
                }
                final Profile profile2 = this.profilesById.get(profileId2);
                activeFromPom.add(profile2);
            }
        }
        final List allActive = new ArrayList(activeFromPom.size() + activeExternal.size());
        allActive.addAll(activeExternal);
        allActive.addAll(activeFromPom);
        return allActive;
    }
    
    private boolean isActive(final Profile profile) throws ProfileActivationException {
        List activators = null;
        final Properties systemProperties = new Properties(System.getProperties());
        if (this.requestProperties != null) {
            systemProperties.putAll(this.requestProperties);
        }
        this.container.addContextValue("SystemProperties", systemProperties);
        try {
            activators = this.container.lookupList(ProfileActivator.ROLE);
            for (final ProfileActivator activator : activators) {
                if (activator.canDetermineActivation(profile) && activator.isActive(profile)) {
                    return true;
                }
            }
            return false;
        }
        catch (ComponentLookupException e) {
            throw new ProfileActivationException("Cannot retrieve list of profile activators.", e);
        }
        finally {
            this.container.getContext().put("SystemProperties", null);
            if (activators != null) {
                try {
                    this.container.releaseAll(activators);
                }
                catch (ComponentLifecycleException e2) {
                    this.container.getLogger().debug("Error releasing profile activators - ignoring.", e2);
                }
            }
        }
    }
    
    public void addProfiles(final List profiles) {
        for (final Profile profile : profiles) {
            this.addProfile(profile);
        }
    }
    
    public void activateAsDefault(final String profileId) {
        if (!this.defaultIds.contains(profileId)) {
            this.defaultIds.add(profileId);
        }
    }
    
    public List getExplicitlyActivatedIds() {
        return this.activatedIds;
    }
    
    public List getExplicitlyDeactivatedIds() {
        return this.deactivatedIds;
    }
    
    public List getIdsActivatedByDefault() {
        return this.defaultIds;
    }
    
    public void loadSettingsProfiles(final Settings settings) {
        if (settings == null) {
            return;
        }
        final List settingsProfiles = settings.getProfiles();
        final List settingsActiveProfileIds = settings.getActiveProfiles();
        this.explicitlyActivate(settingsActiveProfileIds);
        if (settingsProfiles != null && !settingsProfiles.isEmpty()) {
            for (final org.apache.maven.settings.Profile rawProfile : settings.getProfiles()) {
                final Profile profile = SettingsUtils.convertFromSettingsProfile(rawProfile);
                this.addProfile(profile);
            }
        }
    }
}
