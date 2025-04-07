// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.execute;

import java.util.logging.Level;
import org.pitest.util.Log;
import java.util.Collections;
import org.pitest.testapi.execute.MultipleTestGroup;
import org.pitest.testapi.Description;
import org.pitest.util.Unchecked;
import org.pitest.testapi.TestListener;
import org.pitest.testapi.execute.Pitest;
import org.pitest.testapi.ResultCollector;
import org.pitest.testapi.execute.ExitingResultCollector;
import org.pitest.testapi.execute.containers.ConcreteResultCollector;
import java.util.ArrayList;
import org.pitest.testapi.TestResult;
import org.pitest.testapi.execute.containers.UnContainer;
import org.pitest.testapi.execute.Container;
import org.pitest.mutationtest.DetectionStatus;
import org.pitest.mutationtest.MutationStatusTestPair;
import org.pitest.testapi.TestUnit;
import java.util.List;
import org.pitest.mutationtest.engine.Mutant;
import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.mutationtest.mocksupport.JavassistInterceptor;
import java.io.IOException;
import java.util.Iterator;
import org.pitest.mutationtest.engine.MutationDetails;
import java.util.Collection;
import org.pitest.classinfo.ClassName;
import org.pitest.functional.F3;
import org.pitest.mutationtest.engine.Mutater;
import java.util.logging.Logger;

public class MutationTestWorker
{
    private static final Logger LOG;
    private static final boolean DEBUG;
    private final Mutater mutater;
    private final ClassLoader loader;
    private final F3<ClassName, ClassLoader, byte[], Boolean> hotswap;
    
    public MutationTestWorker(final F3<ClassName, ClassLoader, byte[], Boolean> hotswap, final Mutater mutater, final ClassLoader loader) {
        this.loader = loader;
        this.mutater = mutater;
        this.hotswap = hotswap;
    }
    
    protected void run(final Collection<MutationDetails> range, final Reporter r, final TimeOutDecoratedTestSource testSource) throws IOException {
        for (final MutationDetails mutation : range) {
            if (MutationTestWorker.DEBUG) {
                MutationTestWorker.LOG.fine("Running mutation " + mutation);
            }
            final long t0 = System.currentTimeMillis();
            this.processMutation(r, testSource, mutation);
            if (MutationTestWorker.DEBUG) {
                MutationTestWorker.LOG.fine("processed mutation in " + (System.currentTimeMillis() - t0) + " ms.");
            }
        }
    }
    
    private void processMutation(final Reporter r, final TimeOutDecoratedTestSource testSource, final MutationDetails mutationDetails) throws IOException {
        final MutationIdentifier mutationId = mutationDetails.getId();
        final Mutant mutatedClass = this.mutater.getMutation(mutationId);
        JavassistInterceptor.setMutant(mutatedClass);
        if (MutationTestWorker.DEBUG) {
            MutationTestWorker.LOG.fine("mutating method " + mutatedClass.getDetails().getMethod());
        }
        final List<TestUnit> relevantTests = testSource.translateTests(mutationDetails.getTestsInOrder());
        r.describe(mutationId);
        final MutationStatusTestPair mutationDetected = this.handleMutation(mutationDetails, mutatedClass, relevantTests);
        r.report(mutationId, mutationDetected);
        if (MutationTestWorker.DEBUG) {
            MutationTestWorker.LOG.fine("Mutation " + mutationId + " detected = " + mutationDetected);
        }
    }
    
    private MutationStatusTestPair handleMutation(final MutationDetails mutationId, final Mutant mutatedClass, final List<TestUnit> relevantTests) {
        MutationStatusTestPair mutationDetected;
        if (relevantTests == null || relevantTests.isEmpty()) {
            MutationTestWorker.LOG.info("No test coverage for mutation  " + mutationId + " in " + mutatedClass.getDetails().getMethod());
            mutationDetected = new MutationStatusTestPair(0, DetectionStatus.RUN_ERROR);
        }
        else {
            mutationDetected = this.handleCoveredMutation(mutationId, mutatedClass, relevantTests);
        }
        return mutationDetected;
    }
    
    private MutationStatusTestPair handleCoveredMutation(final MutationDetails mutationId, final Mutant mutatedClass, final List<TestUnit> relevantTests) {
        if (MutationTestWorker.DEBUG) {
            MutationTestWorker.LOG.fine("" + relevantTests.size() + " relevant test for " + mutatedClass.getDetails().getMethod());
        }
        final Container c = createNewContainer();
        final long t0 = System.currentTimeMillis();
        MutationStatusTestPair mutationDetected;
        if (this.hotswap.apply(mutationId.getClassName(), this.loader, mutatedClass.getBytes())) {
            if (MutationTestWorker.DEBUG) {
                MutationTestWorker.LOG.fine("replaced class with mutant in " + (System.currentTimeMillis() - t0) + " ms");
            }
            mutationDetected = this.doTestsDetectMutation(c, relevantTests);
        }
        else {
            MutationTestWorker.LOG.warning("Mutation " + mutationId + " was not viable ");
            mutationDetected = new MutationStatusTestPair(0, DetectionStatus.NON_VIABLE);
        }
        return mutationDetected;
    }
    
    private static Container createNewContainer() {
        final Container c = new UnContainer() {
            @Override
            public List<TestResult> execute(final TestUnit group) {
                final List<TestResult> results = new ArrayList<TestResult>();
                final ExitingResultCollector rc = new ExitingResultCollector(new ConcreteResultCollector(results));
                group.execute(rc);
                return results;
            }
        };
        return c;
    }
    
    @Override
    public String toString() {
        return "MutationTestWorker [mutater=" + this.mutater + ", loader=" + this.loader + ", hotswap=" + this.hotswap + "]";
    }
    
    private MutationStatusTestPair doTestsDetectMutation(final Container c, final List<TestUnit> tests) {
        try {
            final CheckTestHasFailedResultListener listener = new CheckTestHasFailedResultListener();
            final Pitest pit = new Pitest(listener);
            pit.run(c, this.createEarlyExitTestGroup(tests));
            return this.createStatusTestPair(listener);
        }
        catch (Exception ex) {
            throw Unchecked.translateCheckedException(ex);
        }
    }
    
    private MutationStatusTestPair createStatusTestPair(final CheckTestHasFailedResultListener listener) {
        if (listener.lastFailingTest().hasSome()) {
            return new MutationStatusTestPair(listener.getNumberOfTestsRun(), listener.status(), listener.lastFailingTest().value().getQualifiedName());
        }
        return new MutationStatusTestPair(listener.getNumberOfTestsRun(), listener.status());
    }
    
    private List<TestUnit> createEarlyExitTestGroup(final List<TestUnit> tests) {
        return (List<TestUnit>)Collections.singletonList(new MultipleTestGroup(tests));
    }
    
    static {
        LOG = Log.getLogger();
        DEBUG = MutationTestWorker.LOG.isLoggable(Level.FINE);
    }
}
