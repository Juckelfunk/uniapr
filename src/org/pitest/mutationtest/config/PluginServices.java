// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.config;

import org.pitest.mutationtest.build.MutationInterceptorFactory;
import org.pitest.mutationtest.build.TestPrioritiserFactory;
import org.pitest.mutationtest.MutationEngineFactory;
import org.pitest.mutationtest.MutationResultListenerFactory;
import org.pitest.mutationtest.build.MutationGrouperFactory;
import org.pitest.util.ServiceLoader;
import org.pitest.testapi.TestPluginFactory;
import org.pitest.plugin.ClientClasspathPlugin;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import org.pitest.plugin.ToolClasspathPlugin;
import org.pitest.util.IsolationUtils;

public class PluginServices
{
    private final ClassLoader loader;
    
    public PluginServices(final ClassLoader loader) {
        this.loader = loader;
    }
    
    public static PluginServices makeForContextLoader() {
        return new PluginServices(IsolationUtils.getContextClassLoader());
    }
    
    public Iterable<? extends ToolClasspathPlugin> findToolClasspathPlugins() {
        final List<ToolClasspathPlugin> l = new ArrayList<ToolClasspathPlugin>();
        l.addAll(this.findListeners());
        l.addAll(this.findGroupers());
        l.addAll(this.findTestPrioritisers());
        l.addAll(this.findInterceptors());
        return l;
    }
    
    public Iterable<? extends ClientClasspathPlugin> findClientClasspathPlugins() {
        final List<ClientClasspathPlugin> l = new ArrayList<ClientClasspathPlugin>();
        l.addAll(this.findMutationEngines());
        l.addAll(this.findTestFrameworkPlugins());
        l.addAll(this.nullPlugins());
        return l;
    }
    
    Collection<? extends TestPluginFactory> findTestFrameworkPlugins() {
        return ServiceLoader.load((Class<? extends TestPluginFactory>)TestPluginFactory.class, this.loader);
    }
    
    Collection<? extends MutationGrouperFactory> findGroupers() {
        return ServiceLoader.load((Class<? extends MutationGrouperFactory>)MutationGrouperFactory.class, this.loader);
    }
    
    Collection<? extends MutationResultListenerFactory> findListeners() {
        return ServiceLoader.load((Class<? extends MutationResultListenerFactory>)MutationResultListenerFactory.class, this.loader);
    }
    
    Collection<? extends MutationEngineFactory> findMutationEngines() {
        return ServiceLoader.load((Class<? extends MutationEngineFactory>)MutationEngineFactory.class, this.loader);
    }
    
    Collection<? extends TestPrioritiserFactory> findTestPrioritisers() {
        return ServiceLoader.load((Class<? extends TestPrioritiserFactory>)TestPrioritiserFactory.class, this.loader);
    }
    
    private Collection<ClientClasspathPlugin> nullPlugins() {
        return ServiceLoader.load(ClientClasspathPlugin.class, this.loader);
    }
    
    public Collection<? extends MutationInterceptorFactory> findInterceptors() {
        return ServiceLoader.load((Class<? extends MutationInterceptorFactory>)MutationInterceptorFactory.class, this.loader);
    }
}
