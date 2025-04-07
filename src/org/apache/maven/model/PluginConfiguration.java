// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.io.Serializable;

public class PluginConfiguration extends PluginContainer implements Serializable
{
    private PluginManagement pluginManagement;
    
    public PluginManagement getPluginManagement() {
        return this.pluginManagement;
    }
    
    public void setPluginManagement(final PluginManagement pluginManagement) {
        this.pluginManagement = pluginManagement;
    }
}
