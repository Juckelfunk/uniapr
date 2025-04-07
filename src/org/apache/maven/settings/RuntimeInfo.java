// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.settings;

import java.util.HashMap;
import java.util.Map;
import java.io.File;

public class RuntimeInfo
{
    private File file;
    private Boolean pluginUpdateForced;
    private Boolean applyToAllPluginUpdates;
    private Map activeProfileToSourceLevel;
    private String localRepositorySourceLevel;
    private Map pluginGroupIdSourceLevels;
    private final Settings settings;
    
    public RuntimeInfo(final Settings settings) {
        this.activeProfileToSourceLevel = new HashMap();
        this.localRepositorySourceLevel = "user-level";
        this.pluginGroupIdSourceLevels = new HashMap();
        this.settings = settings;
    }
    
    public void setFile(final File file) {
        this.file = file;
    }
    
    public File getFile() {
        return this.file;
    }
    
    public void setPluginUpdateOverride(final Boolean pluginUpdateForced) {
        this.pluginUpdateForced = pluginUpdateForced;
    }
    
    public Boolean getPluginUpdateOverride() {
        return this.pluginUpdateForced;
    }
    
    public Boolean getApplyToAllPluginUpdates() {
        return this.applyToAllPluginUpdates;
    }
    
    public void setApplyToAllPluginUpdates(final Boolean applyToAll) {
        this.applyToAllPluginUpdates = applyToAll;
    }
    
    public void setActiveProfileSourceLevel(final String activeProfile, final String sourceLevel) {
        this.activeProfileToSourceLevel.put(activeProfile, sourceLevel);
    }
    
    public String getSourceLevelForActiveProfile(final String activeProfile) {
        final String sourceLevel = this.activeProfileToSourceLevel.get(activeProfile);
        if (sourceLevel != null) {
            return sourceLevel;
        }
        return this.settings.getSourceLevel();
    }
    
    public void setPluginGroupIdSourceLevel(final String pluginGroupId, final String sourceLevel) {
        this.pluginGroupIdSourceLevels.put(pluginGroupId, sourceLevel);
    }
    
    public String getSourceLevelForPluginGroupId(final String pluginGroupId) {
        final String sourceLevel = this.pluginGroupIdSourceLevels.get(pluginGroupId);
        if (sourceLevel != null) {
            return sourceLevel;
        }
        return this.settings.getSourceLevel();
    }
    
    public void setLocalRepositorySourceLevel(final String localRepoSourceLevel) {
        this.localRepositorySourceLevel = localRepoSourceLevel;
    }
    
    public String getLocalRepositorySourceLevel() {
        return this.localRepositorySourceLevel;
    }
}
