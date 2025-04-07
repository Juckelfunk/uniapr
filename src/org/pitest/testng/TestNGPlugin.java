// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.testng;

import org.pitest.testapi.Configuration;
import java.util.Collection;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.testapi.TestGroupConfig;
import org.pitest.testapi.TestPluginFactory;

public class TestNGPlugin implements TestPluginFactory
{
    @Override
    public String description() {
        return "TestNG plugin";
    }
    
    @Override
    public Configuration createTestFrameworkConfiguration(final TestGroupConfig config, final ClassByteArraySource source, final Collection<String> excludedRunner, final Collection<String> includedTestMethods) {
        return new TestNGConfiguration(config, includedTestMethods);
    }
    
    @Override
    public String name() {
        return "testng";
    }
}
