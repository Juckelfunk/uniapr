// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.config;

import java.util.Collections;
import org.pitest.util.Preconditions;
import java.util.Collection;
import org.pitest.testapi.TestGroupConfig;
import java.io.Serializable;

public class TestPluginArguments implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final String testPlugin;
    private final TestGroupConfig groupConfig;
    private final Collection<String> includedTestMethods;
    private final Collection<String> excludedRunners;
    
    public TestPluginArguments(final String testPlugin, final TestGroupConfig groupConfig, final Collection<String> excludedRunners, final Collection<String> includedTestMethods) {
        Preconditions.checkNotNull(testPlugin);
        Preconditions.checkNotNull(groupConfig);
        Preconditions.checkNotNull(excludedRunners);
        this.testPlugin = testPlugin;
        this.groupConfig = groupConfig;
        this.excludedRunners = excludedRunners;
        this.includedTestMethods = includedTestMethods;
    }
    
    public static TestPluginArguments defaults() {
        return new TestPluginArguments("junit", new TestGroupConfig(), (Collection<String>)Collections.emptyList(), (Collection<String>)Collections.emptyList());
    }
    
    public TestPluginArguments withTestPlugin(final String plugin) {
        return new TestPluginArguments(plugin, this.groupConfig, this.excludedRunners, this.includedTestMethods);
    }
    
    public TestGroupConfig getGroupConfig() {
        return this.groupConfig;
    }
    
    public Collection<String> getExcludedRunners() {
        return this.excludedRunners;
    }
    
    public Collection<String> getIncludedTestMethods() {
        return this.includedTestMethods;
    }
    
    public String getTestPlugin() {
        return this.testPlugin;
    }
}
