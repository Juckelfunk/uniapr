// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.testapi.execute;

import org.pitest.util.Log;
import org.pitest.util.PitError;
import java.util.Collection;
import java.util.Arrays;
import org.pitest.testapi.Configuration;
import org.pitest.testapi.TestResult;
import java.util.Iterator;
import org.pitest.testapi.TestUnit;
import java.util.List;
import org.pitest.testapi.TestListener;
import java.util.logging.Logger;

public class Pitest
{
    private static final Logger LOG;
    private final TestListener listener;
    
    public Pitest(final TestListener listener) {
        this.listener = listener;
    }
    
    public void run(final Container container, final List<? extends TestUnit> testUnits) {
        Pitest.LOG.fine("Running " + testUnits.size() + " units");
        this.signalRunStartToAllListeners();
        this.executeTests(container, testUnits);
        this.signalRunEndToAllListeners();
    }
    
    private void executeTests(final Container container, final List<? extends TestUnit> testUnits) {
        for (final TestUnit unit : testUnits) {
            final List<TestResult> results = container.execute(unit);
            this.processResults(results);
        }
    }
    
    @Deprecated
    public void run(final Container defaultContainer, final Configuration config, final Class<?>... classes) {
        this.run(defaultContainer, config, Arrays.asList(classes));
    }
    
    private void run(final Container container, final Configuration config, final Collection<Class<?>> classes) {
        final FindTestUnits find = new FindTestUnits(config);
        this.run(container, find.findTestUnitsForAllSuppliedClasses(classes));
    }
    
    private void processResults(final List<TestResult> results) {
        for (final TestResult result : results) {
            final ResultType classifiedResult = this.classify(result);
            classifiedResult.getListenerFunction(result).apply(this.listener);
        }
    }
    
    private void signalRunStartToAllListeners() {
        this.listener.onRunStart();
    }
    
    private void signalRunEndToAllListeners() {
        this.listener.onRunEnd();
    }
    
    private ResultType classify(final TestResult result) {
        switch (result.getState()) {
            case STARTED: {
                return ResultType.STARTED;
            }
            case NOT_RUN: {
                return ResultType.SKIPPED;
            }
            case FINISHED: {
                return this.classifyFinishedTest(result);
            }
            default: {
                throw new PitError("Unhandled state");
            }
        }
    }
    
    private ResultType classifyFinishedTest(final TestResult result) {
        if (result.getThrowable() != null) {
            return ResultType.FAIL;
        }
        return ResultType.PASS;
    }
    
    static {
        LOG = Log.getLogger();
    }
}
