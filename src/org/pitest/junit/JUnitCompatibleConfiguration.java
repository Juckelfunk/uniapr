// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.junit;

import org.pitest.help.Help;
import junit.runner.Version;
import org.pitest.help.PitHelpError;
import org.pitest.functional.Option;
import org.pitest.extension.common.CompoundTestSuiteFinder;
import org.pitest.testapi.TestSuiteFinder;
import java.util.Arrays;
import org.pitest.testapi.TestUnitFinder;
import org.pitest.util.Preconditions;
import java.util.Collection;
import org.pitest.testapi.TestGroupConfig;
import org.pitest.testapi.Configuration;

public class JUnitCompatibleConfiguration implements Configuration
{
    private final TestGroupConfig config;
    private final Collection<String> excludedRunners;
    private final Collection<String> includedTestMethods;
    private static final JUnitVersion MIN_JUNIT_VERSION;
    
    public JUnitCompatibleConfiguration(final TestGroupConfig config, final Collection<String> excludedRunners, final Collection<String> includedTestMethods) {
        Preconditions.checkNotNull(config);
        this.config = config;
        this.excludedRunners = excludedRunners;
        this.includedTestMethods = includedTestMethods;
    }
    
    @Override
    public TestUnitFinder testUnitFinder() {
        return new CompoundTestUnitFinder(Arrays.asList(new JUnitCustomRunnerTestUnitFinder(this.config, this.excludedRunners, this.includedTestMethods), new ParameterisedJUnitTestFinder()));
    }
    
    @Override
    public TestSuiteFinder testSuiteFinder() {
        return new CompoundTestSuiteFinder(Arrays.asList(new JUnit4SuiteFinder(), new RunnerSuiteFinder()));
    }
    
    @Override
    public Option<PitHelpError> verifyEnvironment() {
        try {
            final String version = Version.id();
            if (this.isInvalidVersion(version)) {
                return Option.some(new PitHelpError(Help.WRONG_JUNIT_VERSION, new Object[] { version }));
            }
        }
        catch (NoClassDefFoundError er) {
            return Option.some(new PitHelpError(Help.NO_JUNIT, new Object[0]));
        }
        return (Option<PitHelpError>)Option.none();
    }
    
    boolean isInvalidVersion(final String version) {
        try {
            final JUnitVersion jUnitVersion = JUnitVersion.parse(version);
            return jUnitVersion.isLessThan(JUnitCompatibleConfiguration.MIN_JUNIT_VERSION);
        }
        catch (IllegalArgumentException e) {
            return true;
        }
    }
    
    static {
        MIN_JUNIT_VERSION = JUnitVersion.parse("4.6");
    }
}
