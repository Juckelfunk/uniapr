// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.plugin.registry;

import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.io.Serializable;

public class PluginRegistry extends TrackableBase implements Serializable
{
    private String updateInterval;
    private String autoUpdate;
    private String checkLatest;
    private List<Plugin> plugins;
    private String modelEncoding;
    private Map pluginsByKey;
    private RuntimeInfo runtimeInfo;
    
    public PluginRegistry() {
        this.updateInterval = "never";
        this.modelEncoding = "UTF-8";
    }
    
    public void addPlugin(final Plugin plugin) {
        if (!(plugin instanceof Plugin)) {
            throw new ClassCastException("PluginRegistry.addPlugins(plugin) parameter must be instanceof " + Plugin.class.getName());
        }
        this.getPlugins().add(plugin);
    }
    
    public String getAutoUpdate() {
        return this.autoUpdate;
    }
    
    public String getCheckLatest() {
        return this.checkLatest;
    }
    
    public String getModelEncoding() {
        return this.modelEncoding;
    }
    
    public List<Plugin> getPlugins() {
        if (this.plugins == null) {
            this.plugins = new ArrayList<Plugin>();
        }
        return this.plugins;
    }
    
    public String getUpdateInterval() {
        return this.updateInterval;
    }
    
    public void removePlugin(final Plugin plugin) {
        if (!(plugin instanceof Plugin)) {
            throw new ClassCastException("PluginRegistry.removePlugins(plugin) parameter must be instanceof " + Plugin.class.getName());
        }
        this.getPlugins().remove(plugin);
    }
    
    public void setAutoUpdate(final String autoUpdate) {
        this.autoUpdate = autoUpdate;
    }
    
    public void setCheckLatest(final String checkLatest) {
        this.checkLatest = checkLatest;
    }
    
    public void setModelEncoding(final String modelEncoding) {
        this.modelEncoding = modelEncoding;
    }
    
    public void setPlugins(final List<Plugin> plugins) {
        this.plugins = plugins;
    }
    
    public void setUpdateInterval(final String updateInterval) {
        this.updateInterval = updateInterval;
    }
    
    public Map getPluginsByKey() {
        if (this.pluginsByKey == null) {
            this.pluginsByKey = new HashMap();
            for (final Plugin plugin : this.getPlugins()) {
                this.pluginsByKey.put(plugin.getKey(), plugin);
            }
        }
        return this.pluginsByKey;
    }
    
    public void flushPluginsByKey() {
        this.pluginsByKey = null;
    }
    
    public void setRuntimeInfo(final RuntimeInfo runtimeInfo) {
        this.runtimeInfo = runtimeInfo;
    }
    
    public RuntimeInfo getRuntimeInfo() {
        return this.runtimeInfo;
    }
}
