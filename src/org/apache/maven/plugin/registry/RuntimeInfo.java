// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.plugin.registry;

import java.io.File;

public class RuntimeInfo
{
    private File file;
    private String autoUpdateSourceLevel;
    private String updateIntervalSourceLevel;
    private final PluginRegistry registry;
    
    public RuntimeInfo(final PluginRegistry registry) {
        this.registry = registry;
    }
    
    public String getAutoUpdateSourceLevel() {
        if (this.autoUpdateSourceLevel == null) {
            return this.registry.getSourceLevel();
        }
        return this.autoUpdateSourceLevel;
    }
    
    public void setAutoUpdateSourceLevel(final String autoUpdateSourceLevel) {
        this.autoUpdateSourceLevel = autoUpdateSourceLevel;
    }
    
    public File getFile() {
        return this.file;
    }
    
    public void setFile(final File file) {
        this.file = file;
    }
    
    public String getUpdateIntervalSourceLevel() {
        if (this.updateIntervalSourceLevel == null) {
            return this.registry.getSourceLevel();
        }
        return this.updateIntervalSourceLevel;
    }
    
    public void setUpdateIntervalSourceLevel(final String updateIntervalSourceLevel) {
        this.updateIntervalSourceLevel = updateIntervalSourceLevel;
    }
}
