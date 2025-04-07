// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.plugin.registry;

import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;

public final class PluginRegistryUtils
{
    private PluginRegistryUtils() {
    }
    
    public static void merge(final PluginRegistry dominant, final PluginRegistry recessive, final String recessiveSourceLevel) {
        if (dominant == null || recessive == null) {
            return;
        }
        final RuntimeInfo dominantRtInfo = dominant.getRuntimeInfo();
        final String dominantUpdateInterval = dominant.getUpdateInterval();
        if (dominantUpdateInterval == null) {
            final String recessiveUpdateInterval = recessive.getUpdateInterval();
            if (recessiveUpdateInterval != null) {
                dominant.setUpdateInterval(recessiveUpdateInterval);
                dominantRtInfo.setUpdateIntervalSourceLevel(recessiveSourceLevel);
            }
        }
        final String dominantAutoUpdate = dominant.getAutoUpdate();
        if (dominantAutoUpdate == null) {
            final String recessiveAutoUpdate = recessive.getAutoUpdate();
            if (recessiveAutoUpdate != null) {
                dominant.setAutoUpdate(recessiveAutoUpdate);
                dominantRtInfo.setAutoUpdateSourceLevel(recessiveSourceLevel);
            }
        }
        List recessivePlugins = null;
        if (recessive != null) {
            recessivePlugins = recessive.getPlugins();
        }
        else {
            recessivePlugins = Collections.EMPTY_LIST;
        }
        shallowMergePlugins(dominant, recessivePlugins, recessiveSourceLevel);
    }
    
    public static void recursivelySetSourceLevel(final PluginRegistry pluginRegistry, final String sourceLevel) {
        if (pluginRegistry == null) {
            return;
        }
        pluginRegistry.setSourceLevel(sourceLevel);
        for (final Plugin plugin : pluginRegistry.getPlugins()) {
            plugin.setSourceLevel(sourceLevel);
        }
    }
    
    private static void shallowMergePlugins(final PluginRegistry dominant, final List recessive, final String recessiveSourceLevel) {
        final Map dominantByKey = dominant.getPluginsByKey();
        final List dominantPlugins = dominant.getPlugins();
        for (final Plugin recessivePlugin : recessive) {
            if (!dominantByKey.containsKey(recessivePlugin.getKey())) {
                recessivePlugin.setSourceLevel(recessiveSourceLevel);
                dominantPlugins.add(recessivePlugin);
            }
        }
        dominant.flushPluginsByKey();
    }
    
    public static PluginRegistry extractUserPluginRegistry(final PluginRegistry pluginRegistry) {
        PluginRegistry userRegistry = null;
        if (pluginRegistry != null && !"global-level".equals(pluginRegistry.getSourceLevel())) {
            userRegistry = new PluginRegistry();
            final RuntimeInfo rtInfo = new RuntimeInfo(userRegistry);
            userRegistry.setRuntimeInfo(rtInfo);
            final RuntimeInfo oldRtInfo = pluginRegistry.getRuntimeInfo();
            if ("user-level".equals(oldRtInfo.getAutoUpdateSourceLevel())) {
                userRegistry.setAutoUpdate(pluginRegistry.getAutoUpdate());
            }
            if ("user-level".equals(oldRtInfo.getUpdateIntervalSourceLevel())) {
                userRegistry.setUpdateInterval(pluginRegistry.getUpdateInterval());
            }
            final List plugins = new ArrayList();
            for (final Plugin plugin : pluginRegistry.getPlugins()) {
                if ("user-level".equals(plugin.getSourceLevel())) {
                    plugins.add(plugin);
                }
            }
            userRegistry.setPlugins(plugins);
            rtInfo.setFile(pluginRegistry.getRuntimeInfo().getFile());
        }
        return userRegistry;
    }
}
