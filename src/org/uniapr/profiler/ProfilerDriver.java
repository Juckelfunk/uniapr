// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr.profiler;

import java.io.Serializable;
import java.util.Set;
import java.util.Collections;
import java.util.HashSet;
import org.pitest.functional.predicate.Predicate;
import java.util.Map;
import org.pitest.process.ProcessArgs;
import java.util.Iterator;
import org.pitest.testapi.Description;
import org.uniapr.commons.misc.MemberNameUtils;
import org.pitest.testapi.ResultCollector;
import java.util.LinkedList;
import org.pitest.testapi.TestUnit;
import java.util.List;
import org.pitest.util.ExitCode;
import java.util.Collection;
import org.uniapr.junit.runner.JUnitRunner;
import org.pitest.util.SafeDataInputStream;
import java.net.Socket;

public class ProfilerDriver
{
    public static void main(final String[] args) throws Exception {
        System.out.println("Profiler is HERE!");
        final int port = Integer.parseInt(args[0]);
        try (final Socket socket = new Socket("localhost", port)) {
            final SafeDataInputStream dis = new SafeDataInputStream(socket.getInputStream());
            final ProfilerArguments arguments = dis.read(ProfilerArguments.class);
            final ProfilerReporter reporter = new ProfilerReporter(socket.getOutputStream());
            final JUnitRunner runner = new JUnitRunner(arguments.getAppClassNames(), false);
            runner.setTestUnits(decorateTestUnits(runner.getTestUnits(), reporter));
            runner.run();
            reporter.reportFailingTestNames(runner.getFailingTestNames());
            System.out.println("Profiler is DONE!");
            reporter.done(ExitCode.OK);
        }
    }
    
    private static List<TestUnit> decorateTestUnits(final List<TestUnit> testUnits, final ProfilerReporter reporter) {
        final List<TestUnit> decoratedTests = new LinkedList<TestUnit>();
        for (final TestUnit testUnit : testUnits) {
            decoratedTests.add(new TestUnit() {
                @Override
                public void execute(final ResultCollector rc) {
                    final long start = System.currentTimeMillis();
                    testUnit.execute(rc);
                    final long elapsed = System.currentTimeMillis() - start;
                    final String testName = MemberNameUtils.sanitizeExtendedTestName(testUnit.getDescription().getName());
                    reporter.reportTestTime(testName, elapsed);
                }
                
                @Override
                public Description getDescription() {
                    return testUnit.getDescription();
                }
            });
        }
        return decoratedTests;
    }
    
    public static ProfilerResults runProfiler(final ProcessArgs defaultProcessArgs, final List<String> appClassNames) {
        final ProfilerArguments arguments = new ProfilerArguments(appClassNames);
        final ProfilerProcess process = new ProfilerProcess(defaultProcessArgs, arguments);
        try {
            process.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        process.waitToDie();
        return new ProfilerResults() {
            @Override
            public Map<String, Long> getTestsTiming() {
                return process.getTestsTiming();
            }
            
            @Override
            public Predicate<String> getIsFailingTestPredicate() {
                final Set<String> failingTests = new HashSet<String>();
                Collections.addAll(failingTests, process.getFailingTestNames());
                return new FailingTestPredicate(failingTests);
            }
        };
    }
    
    private static class FailingTestPredicate implements Predicate<String>, Serializable
    {
        private static final long serialVersionUID = 1L;
        private final Set<String> failingTests;
        
        public FailingTestPredicate(final Set<String> failingTests) {
            this.failingTests = failingTests;
        }
        
        @Override
        public Boolean apply(final String testName) {
            return this.failingTests.contains(testName);
        }
    }
}
