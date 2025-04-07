// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.io.Serializable;

public class PluginContainer implements Serializable
{
    private List<Plugin> plugins;
    Map pluginMap;
    
    public void addPlugin(final Plugin plugin) {
        if (!(plugin instanceof Plugin)) {
            throw new ClassCastException("PluginContainer.addPlugins(plugin) parameter must be instanceof " + Plugin.class.getName());
        }
        this.getPlugins().add(plugin);
    }
    
    public List<Plugin> getPlugins() {
        if (this.plugins == null) {
            this.plugins = new ArrayList<Plugin>();
        }
        return this.plugins;
    }
    
    public void removePlugin(final Plugin plugin) {
        if (!(plugin instanceof Plugin)) {
            throw new ClassCastException("PluginContainer.removePlugins(plugin) parameter must be instanceof " + Plugin.class.getName());
        }
        this.getPlugins().remove(plugin);
    }
    
    public void setPlugins(final List<Plugin> plugins) {
        this.plugins = plugins;
    }
    
    public void flushPluginMap() {
        this.pluginMap = null;
    }
    
    public Map getPluginsAsMap() {
        if (this.pluginMap == null) {
            this.pluginMap = new LinkedHashMap();
            if (this.plugins != null) {
                for (final Plugin plugin : this.plugins) {
                    this.pluginMap.put(plugin.getKey(), plugin);
                }
            }
        }
        return this.pluginMap;
    }
}
