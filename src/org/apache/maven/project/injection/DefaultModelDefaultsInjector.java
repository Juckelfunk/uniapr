// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project.injection;

import org.apache.maven.model.Dependency;
import java.util.TreeMap;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.project.ModelUtils;
import java.util.Iterator;
import java.util.Map;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.Build;
import java.util.List;
import org.apache.maven.model.Model;

public class DefaultModelDefaultsInjector implements ModelDefaultsInjector
{
    public void injectDefaults(final Model model) {
        this.injectDependencyDefaults(model.getDependencies(), model.getDependencyManagement());
        if (model.getBuild() != null) {
            this.injectPluginDefaults(model.getBuild(), model.getBuild().getPluginManagement());
        }
    }
    
    private void injectPluginDefaults(final Build build, final PluginManagement pluginManagement) {
        if (pluginManagement == null) {
            return;
        }
        final List buildPlugins = build.getPlugins();
        if (buildPlugins != null && !buildPlugins.isEmpty()) {
            final Map pmPlugins = pluginManagement.getPluginsAsMap();
            if (pmPlugins != null && !pmPlugins.isEmpty()) {
                for (final Plugin buildPlugin : buildPlugins) {
                    final Plugin pmPlugin = pmPlugins.get(buildPlugin.getKey());
                    if (pmPlugin != null) {
                        this.mergePluginWithDefaults(buildPlugin, pmPlugin);
                    }
                }
            }
        }
    }
    
    public void mergePluginWithDefaults(final Plugin plugin, final Plugin def) {
        ModelUtils.mergePluginDefinitions(plugin, def, false);
    }
    
    private void injectDependencyDefaults(final List dependencies, final DependencyManagement dependencyManagement) {
        if (dependencyManagement != null) {
            final Map depsMap = new TreeMap();
            for (final Dependency dep : dependencies) {
                depsMap.put(dep.getManagementKey(), dep);
            }
            final List managedDependencies = dependencyManagement.getDependencies();
            for (final Dependency def : managedDependencies) {
                final String key = def.getManagementKey();
                final Dependency dep2 = depsMap.get(key);
                if (dep2 != null) {
                    this.mergeDependencyWithDefaults(dep2, def);
                }
            }
        }
    }
    
    private void mergeDependencyWithDefaults(final Dependency dep, final Dependency def) {
        if (dep.getScope() == null && def.getScope() != null) {
            dep.setScope(def.getScope());
            dep.setSystemPath(def.getSystemPath());
        }
        if (dep.getVersion() == null && def.getVersion() != null) {
            dep.setVersion(def.getVersion());
        }
        if (dep.getClassifier() == null && def.getClassifier() != null) {
            dep.setClassifier(def.getClassifier());
        }
        if (dep.getType() == null && def.getType() != null) {
            dep.setType(def.getType());
        }
        final List exclusions = dep.getExclusions();
        if (exclusions == null || exclusions.isEmpty()) {
            dep.setExclusions(def.getExclusions());
        }
    }
}
