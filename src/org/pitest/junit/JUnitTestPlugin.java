// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.junit;

import org.pitest.util.Preconditions;
import org.pitest.testapi.Configuration;
import java.util.Collection;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.testapi.TestGroupConfig;
import org.pitest.testapi.TestPluginFactory;

public class JUnitTestPlugin implements TestPluginFactory
{
    public static final String NAME = "junit";
    
    @Override
    public String description() {
        return "JUnit plugin";
    }
    
    @Override
    public Configuration createTestFrameworkConfiguration(final TestGroupConfig config, final ClassByteArraySource source, final Collection<String> excludedRunners, final Collection<String> includedTestMethods) {
        Preconditions.checkNotNull(config);
        return new JUnitCompatibleConfiguration(config, excludedRunners, includedTestMethods);
    }
    
    @Override
    public String name() {
        return "junit";
    }
}
