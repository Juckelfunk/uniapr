// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build;

import org.pitest.util.Log;
import java.util.Comparator;
import java.util.Collections;
import org.pitest.functional.FCollection;
import org.pitest.functional.prelude.Prelude;
import org.pitest.classinfo.ClassName;
import java.util.Collection;
import org.pitest.coverage.TestInfo;
import java.util.List;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.coverage.CoverageDatabase;
import java.util.logging.Logger;

public class DefaultTestPrioritiser implements TestPrioritiser
{
    private static final Logger LOG;
    private static final int TIME_WEIGHTING_FOR_DIRECT_UNIT_TESTS = 1000;
    private final CoverageDatabase coverage;
    
    public DefaultTestPrioritiser(final CoverageDatabase coverage) {
        this.coverage = coverage;
    }
    
    @Override
    public List<TestInfo> assignTests(final MutationDetails mutation) {
        return this.prioritizeTests(mutation.getClassName(), this.pickTests(mutation));
    }
    
    private Collection<TestInfo> pickTests(final MutationDetails mutation) {
        if (!mutation.isInStaticInitializer()) {
            return this.coverage.getTestsForClassLine(mutation.getClassLine());
        }
        DefaultTestPrioritiser.LOG.warning("Using untargetted tests");
        return this.coverage.getTestsForClass(mutation.getClassName());
    }
    
    private List<TestInfo> prioritizeTests(final ClassName clazz, final Collection<TestInfo> testsForMutant) {
        final List<TestInfo> sortedTis = FCollection.map(testsForMutant, Prelude.id(TestInfo.class));
        Collections.sort(sortedTis, new TestInfoPriorisationComparator(clazz, 1000));
        return sortedTis;
    }
    
    static {
        LOG = Log.getLogger();
    }
}
