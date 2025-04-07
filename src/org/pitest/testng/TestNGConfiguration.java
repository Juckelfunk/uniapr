// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.testng;

import org.pitest.help.PitHelpError;
import org.pitest.functional.Option;
import org.pitest.extension.common.NoTestSuiteFinder;
import org.pitest.testapi.TestSuiteFinder;
import org.pitest.testapi.TestUnitFinder;
import java.util.Collection;
import org.pitest.testapi.TestGroupConfig;
import org.pitest.testapi.Configuration;

public class TestNGConfiguration implements Configuration
{
    private final TestGroupConfig config;
    private final Collection<String> includedTestMethods;
    
    public TestNGConfiguration(final TestGroupConfig config, final Collection<String> includedTestMethods) {
        this.config = config;
        this.includedTestMethods = includedTestMethods;
    }
    
    @Override
    public TestUnitFinder testUnitFinder() {
        return new TestNGTestUnitFinder(this.config, this.includedTestMethods);
    }
    
    @Override
    public TestSuiteFinder testSuiteFinder() {
        return new NoTestSuiteFinder();
    }
    
    @Override
    public Option<PitHelpError> verifyEnvironment() {
        return (Option<PitHelpError>)Option.none();
    }
}
