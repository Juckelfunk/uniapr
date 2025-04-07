// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.config;

import org.pitest.mutationtest.MutationEngineFactory;
import org.pitest.util.ServiceLoader;
import org.pitest.testapi.TestPluginFactory;
import java.util.Collection;
import org.pitest.util.IsolationUtils;

public class ClientPluginServices
{
    private final ClassLoader loader;
    
    public ClientPluginServices(final ClassLoader loader) {
        this.loader = loader;
    }
    
    public static ClientPluginServices makeForContextLoader() {
        return new ClientPluginServices(IsolationUtils.getContextClassLoader());
    }
    
    Collection<? extends TestPluginFactory> findTestFrameworkPlugins() {
        return ServiceLoader.load((Class<? extends TestPluginFactory>)TestPluginFactory.class, this.loader);
    }
    
    Collection<? extends MutationEngineFactory> findMutationEngines() {
        return ServiceLoader.load((Class<? extends MutationEngineFactory>)MutationEngineFactory.class, this.loader);
    }
}
