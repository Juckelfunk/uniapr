// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.coverage.execute;

import java.util.Iterator;
import java.util.ArrayList;
import org.pitest.testapi.execute.Container;
import org.pitest.util.Unchecked;
import org.pitest.testapi.TestListener;
import org.pitest.testapi.execute.Pitest;
import org.pitest.testapi.execute.containers.UnContainer;
import java.util.Comparator;
import java.util.Collections;
import org.pitest.coverage.CoverageReceiver;
import org.pitest.testapi.TestUnit;
import java.util.List;

public class CoverageWorker
{
    private final CoveragePipe pipe;
    private final List<TestUnit> tests;
    
    public CoverageWorker(final CoveragePipe pipe, final List<TestUnit> tests) {
        this.pipe = pipe;
        this.tests = tests;
    }
    
    public void run() {
        try {
            final List<TestUnit> decoratedTests = decorateForCoverage(this.tests, this.pipe);
            Collections.sort(decoratedTests, testComparator());
            final Container c = new UnContainer();
            final Pitest pit = new Pitest(new ErrorListener());
            pit.run(c, decoratedTests);
        }
        catch (Exception ex) {
            throw Unchecked.translateCheckedException(ex);
        }
    }
    
    private static Comparator<TestUnit> testComparator() {
        return new Comparator<TestUnit>() {
            @Override
            public int compare(final TestUnit o1, final TestUnit o2) {
                return o1.getDescription().getQualifiedName().compareTo(o2.getDescription().getQualifiedName());
            }
        };
    }
    
    private static List<TestUnit> decorateForCoverage(final List<TestUnit> plainTests, final CoverageReceiver queue) {
        final List<TestUnit> decorated = new ArrayList<TestUnit>(plainTests.size());
        for (final TestUnit each : plainTests) {
            decorated.add(new CoverageDecorator(queue, each));
        }
        return decorated;
    }
}
