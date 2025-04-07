// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr.junit.runner;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import org.uniapr.commons.misc.MemberNameUtils;
import java.util.Iterator;
import org.pitest.functional.predicate.Predicate;
import java.util.Map;
import java.util.Comparator;
import java.util.Collections;
import org.uniapr.validator.PraPRTestComparator;
import java.util.ArrayList;
import org.uniapr.junit.JUnitUtils;
import java.util.Collection;
import org.pitest.testapi.ResultCollector;
import org.pitest.testapi.TestUnit;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class JUnitRunner
{
    private static final ExecutorService EXECUTOR_SERVICE;
    private final List<String> failingTestNames;
    private List<TestUnit> testUnits;
    private final ResultCollector resultCollector;
    
    public JUnitRunner(final Collection<String> classNames, final boolean earlyExit) {
        this.testUnits = JUnitUtils.discoverTestUnits(classNames);
        this.failingTestNames = new ArrayList<String>();
        ResultCollector collector = new DefaultResultCollector(this.failingTestNames);
        if (earlyExit) {
            collector = new ExitingResultCollector(collector);
        }
        this.resultCollector = collector;
    }
    
    public JUnitRunner(final Collection<String> classNames, final PraPRTestComparator comparator, final boolean earlyExit) {
        Collections.sort(this.testUnits = JUnitUtils.discoverTestUnits(classNames), comparator);
        this.failingTestNames = new ArrayList<String>();
        ResultCollector collector = new DefaultResultCollector(this.failingTestNames);
        if (earlyExit) {
            collector = new ExitingResultCollector(collector);
        }
        this.resultCollector = collector;
    }
    
    public List<String> getFailingTestNames() {
        return this.failingTestNames;
    }
    
    public List<TestUnit> getTestUnits() {
        return this.testUnits;
    }
    
    public void setTestUnits(final List<TestUnit> testUnits) {
        this.testUnits = testUnits;
    }
    
    public int run() {
        return this.run(TestUnitFilter.all());
    }
    
    public int run(final Map<String, Long> testsTiming, final long timeoutBias, final double timeoutCoefficient) {
        return this.run(TestUnitFilter.all(), testsTiming, timeoutBias, timeoutCoefficient);
    }
    
    public int run(final Predicate<TestUnit> shouldRun) {
        for (final TestUnit testUnit : this.testUnits) {
            if (!shouldRun.apply(testUnit)) {
                continue;
            }
            testUnit.execute(this.resultCollector);
            if (this.resultCollector.shouldExit()) {
                System.out.println("WARNING: Running test cases is terminated.");
                return 0;
            }
        }
        return 1;
    }
    
    public int run(final Predicate<TestUnit> shouldRun, final Map<String, Long> testsTiming, final long timeoutBias, final double timeoutCoefficient) {
        for (final TestUnit testUnit : this.testUnits) {
            if (!shouldRun.apply(testUnit)) {
                continue;
            }
            final Runnable task = new Runnable() {
                @Override
                public void run() {
                    testUnit.execute(JUnitRunner.this.resultCollector);
                }
            };
            try {
                final String testName = MemberNameUtils.sanitizeExtendedTestName(testUnit.getDescription().getName());
                final long timeoutThreshold = timeoutBias + (long)(testsTiming.get(testName) * (1.0 + timeoutCoefficient));
                JUnitRunner.EXECUTOR_SERVICE.submit(task).get(timeoutThreshold, TimeUnit.MILLISECONDS);
            }
            catch (TimeoutException e) {
                System.out.println("WARNING: Running test cases is terminated due to TIME_OUT.");
                return -1;
            }
            catch (Exception e2) {
                System.out.println("WARNING: Running test cases is terminated.");
                return 0;
            }
            if (this.shouldRestart(this.resultCollector)) {
                System.out.println("WARNING: Running test cases is terminated due to INITIALIZER_ERROR.");
                return -2;
            }
            if (this.resultCollector.shouldExit()) {
                System.out.println("WARNING: Running test cases is terminated.");
                return 0;
            }
        }
        return 1;
    }
    
    public boolean shouldRestart(final ResultCollector resultCollector) {
        if (resultCollector instanceof DefaultResultCollector) {
            return ((DefaultResultCollector)resultCollector).shouldRestart();
        }
        return resultCollector instanceof ExitingResultCollector && ((ExitingResultCollector)resultCollector).shouldRestart();
    }
    
    static {
        EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
    }
}
