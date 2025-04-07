// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.coverage.execute;

import org.pitest.util.Log;
import org.pitest.testapi.execute.ExitingResultCollector;
import org.pitest.testapi.ResultCollector;
import java.lang.management.ManagementFactory;
import org.pitest.testapi.TestUnit;
import java.lang.management.ThreadMXBean;
import org.pitest.coverage.CoverageReceiver;
import java.util.logging.Logger;
import org.pitest.extension.common.TestUnitDecorator;

public class CoverageDecorator extends TestUnitDecorator
{
    private static final Logger LOG;
    private final CoverageReceiver invokeQueue;
    private final ThreadMXBean threads;
    
    protected CoverageDecorator(final CoverageReceiver queue, final TestUnit child) {
        super(child);
        this.threads = ManagementFactory.getThreadMXBean();
        this.invokeQueue = queue;
    }
    
    @Override
    public void execute(final ResultCollector rc) {
        CoverageDecorator.LOG.fine("Gathering coverage for test " + this.child().getDescription());
        this.invokeQueue.newTest();
        final int threadsBeforeTest = this.threads.getThreadCount();
        final long t0 = System.currentTimeMillis();
        final ExitingResultCollector wrappedCollector = new ExitingResultCollector(rc);
        this.child().execute(wrappedCollector);
        final int executionTime = (int)(System.currentTimeMillis() - t0);
        final int threadsAfterTest = this.threads.getThreadCount();
        if (threadsAfterTest > threadsBeforeTest) {
            CoverageDecorator.LOG.warning("More threads at end of test (" + threadsAfterTest + ") " + this.child().getDescription().getName() + " than start. (" + threadsBeforeTest + ")");
        }
        this.invokeQueue.recordTestOutcome(this.child().getDescription(), !wrappedCollector.shouldExit(), executionTime);
    }
    
    static {
        LOG = Log.getLogger();
    }
}
