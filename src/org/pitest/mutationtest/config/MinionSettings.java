// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.config;

import org.pitest.testapi.TestPluginFactory;
import org.pitest.testapi.Configuration;
import org.pitest.classinfo.ClassByteArraySource;
import java.util.Iterator;
import org.pitest.util.PitError;
import org.pitest.mutationtest.MutationEngineFactory;

public class MinionSettings
{
    private final ClientPluginServices plugins;
    
    public MinionSettings(final ClientPluginServices plugins) {
        this.plugins = plugins;
    }
    
    public MutationEngineFactory createEngine(final String engine) {
        for (final MutationEngineFactory each : this.plugins.findMutationEngines()) {
            if (each.name().equals(engine)) {
                return each;
            }
        }
        throw new PitError("Could not load requested engine " + engine);
    }
    
    public Configuration getTestFrameworkPlugin(final TestPluginArguments options, final ClassByteArraySource source) {
        for (final TestPluginFactory each : this.plugins.findTestFrameworkPlugins()) {
            if (each.name().equals(options.getTestPlugin())) {
                return each.createTestFrameworkConfiguration(options.getGroupConfig(), source, options.getExcludedRunners(), options.getIncludedTestMethods());
            }
        }
        throw new PitError("Could not load requested test plugin " + options.getTestPlugin());
    }
}
