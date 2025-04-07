// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.incremental;

import org.pitest.util.Log;
import org.pitest.functional.F;
import org.pitest.coverage.TestInfo;
import org.pitest.functional.FCollection;
import org.pitest.classinfo.ClassName;
import org.pitest.functional.Option;
import java.util.Iterator;
import java.util.List;
import org.pitest.mutationtest.MutationStatusTestPair;
import java.util.ArrayList;
import org.pitest.mutationtest.MutationResult;
import org.pitest.mutationtest.engine.MutationDetails;
import java.util.Collection;
import java.util.EnumMap;
import org.pitest.mutationtest.DetectionStatus;
import java.util.Map;
import org.pitest.coverage.CoverageDatabase;
import java.util.logging.Logger;
import org.pitest.mutationtest.MutationAnalyser;

public class IncrementalAnalyser implements MutationAnalyser
{
    private static final Logger LOG;
    private final CodeHistory history;
    private final CoverageDatabase coverage;
    private final Map<DetectionStatus, Long> preAnalysed;
    
    public IncrementalAnalyser(final CodeHistory history, final CoverageDatabase coverage) {
        this.preAnalysed = createStatusMap();
        this.history = history;
        this.coverage = coverage;
    }
    
    private static Map<DetectionStatus, Long> createStatusMap() {
        final EnumMap<DetectionStatus, Long> map = new EnumMap<DetectionStatus, Long>(DetectionStatus.class);
        for (final DetectionStatus each : DetectionStatus.values()) {
            map.put(each, 0L);
        }
        return map;
    }
    
    @Override
    public Collection<MutationResult> analyse(final Collection<MutationDetails> mutation) {
        final List<MutationResult> mrs = new ArrayList<MutationResult>(mutation.size());
        for (final MutationDetails each : mutation) {
            final Option<MutationStatusTestPair> maybeResult = this.history.getPreviousResult(each.getId());
            if (maybeResult.hasNone()) {
                mrs.add(this.analyseFromScratch(each));
            }
            else {
                mrs.add(this.analyseFromHistory(each, maybeResult.value()));
            }
        }
        this.logTotals();
        return mrs;
    }
    
    private void logTotals() {
        for (final Map.Entry<DetectionStatus, Long> each : this.preAnalysed.entrySet()) {
            if (each.getValue() != 0L) {
                IncrementalAnalyser.LOG.fine("Incremental analysis set " + each.getValue() + " mutations to a status of " + each.getKey());
            }
        }
    }
    
    private MutationResult analyseFromHistory(final MutationDetails each, final MutationStatusTestPair mutationStatusTestPair) {
        final ClassName clazz = each.getClassName();
        if (this.history.hasClassChanged(clazz)) {
            return this.analyseFromScratch(each);
        }
        if (mutationStatusTestPair.getStatus() == DetectionStatus.TIMED_OUT) {
            return this.makeResult(each, DetectionStatus.TIMED_OUT);
        }
        if (mutationStatusTestPair.getStatus() == DetectionStatus.KILLED && this.killingTestHasNotChanged(each, mutationStatusTestPair)) {
            return this.makeResult(each, DetectionStatus.KILLED, mutationStatusTestPair.getKillingTest().value());
        }
        if (mutationStatusTestPair.getStatus() == DetectionStatus.SURVIVED && !this.history.hasCoverageChanged(clazz, this.coverage.getCoverageIdForClass(clazz))) {
            return this.makeResult(each, DetectionStatus.SURVIVED);
        }
        return this.analyseFromScratch(each);
    }
    
    private boolean killingTestHasNotChanged(final MutationDetails each, final MutationStatusTestPair mutationStatusTestPair) {
        final Collection<TestInfo> allTests = this.coverage.getTestsForClass(each.getClassName());
        final List<ClassName> testClasses = FCollection.filter(allTests, testIsCalled(mutationStatusTestPair.getKillingTest().value())).map(TestInfo.toDefiningClassName());
        return !testClasses.isEmpty() && !this.history.hasClassChanged(testClasses.get(0));
    }
    
    private static F<TestInfo, Boolean> testIsCalled(final String testName) {
        return new F<TestInfo, Boolean>() {
            @Override
            public Boolean apply(final TestInfo a) {
                return a.getName().equals(testName);
            }
        };
    }
    
    private MutationResult analyseFromScratch(final MutationDetails mutation) {
        return this.makeResult(mutation, DetectionStatus.NOT_STARTED);
    }
    
    private MutationResult makeResult(final MutationDetails each, final DetectionStatus status) {
        return this.makeResult(each, status, null);
    }
    
    private MutationResult makeResult(final MutationDetails each, final DetectionStatus status, final String killingTest) {
        this.updatePreanalysedTotal(status);
        return new MutationResult(each, new MutationStatusTestPair(0, status, killingTest));
    }
    
    private void updatePreanalysedTotal(final DetectionStatus status) {
        if (status != DetectionStatus.NOT_STARTED) {
            final long count = this.preAnalysed.get(status);
            this.preAnalysed.put(status, count + 1L);
        }
    }
    
    static {
        LOG = Log.getLogger();
    }
}
