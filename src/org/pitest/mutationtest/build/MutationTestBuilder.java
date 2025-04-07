// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build;

import org.pitest.coverage.TestInfo;
import org.pitest.mutationtest.DetectionStatus;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import org.pitest.functional.prelude.Prelude;
import org.pitest.mutationtest.MutationResult;
import org.pitest.mutationtest.engine.MutationDetails;
import java.util.Comparator;
import java.util.Collections;
import org.pitest.functional.F;
import org.pitest.functional.FCollection;
import java.util.ArrayList;
import java.util.List;
import org.pitest.classinfo.ClassName;
import java.util.Collection;
import org.pitest.mutationtest.MutationAnalyser;

public class MutationTestBuilder
{
    private final MutationSource mutationSource;
    private final MutationAnalyser analyser;
    private final WorkerFactory workerFactory;
    private final MutationGrouper grouper;
    
    public MutationTestBuilder(final WorkerFactory workerFactory, final MutationAnalyser analyser, final MutationSource mutationSource, final MutationGrouper grouper) {
        this.mutationSource = mutationSource;
        this.analyser = analyser;
        this.workerFactory = workerFactory;
        this.grouper = grouper;
    }
    
    public List<MutationAnalysisUnit> createMutationTestUnits(final Collection<ClassName> codeClasses) {
        final List<MutationAnalysisUnit> tus = new ArrayList<MutationAnalysisUnit>();
        final List<MutationDetails> mutations = (List<MutationDetails>)FCollection.flatMap((Iterable<? extends ClassName>)codeClasses, (F<ClassName, ? extends Iterable<Object>>)this.classToMutations());
        Collections.sort(mutations, this.comparator());
        final Collection<MutationResult> analysedMutations = this.analyser.analyse(mutations);
        final Collection<MutationDetails> needAnalysis = FCollection.filter(analysedMutations, statusNotKnown()).map(resultToDetails());
        final List<MutationResult> analysed = (List<MutationResult>)FCollection.filter(analysedMutations, (F<Object, Boolean>)Prelude.not(statusNotKnown()));
        if (!analysed.isEmpty()) {
            tus.add(this.makePreAnalysedUnit(analysed));
        }
        if (!needAnalysis.isEmpty()) {
            for (final Collection<MutationDetails> ms : this.grouper.groupMutations(codeClasses, needAnalysis)) {
                tus.add(this.makeUnanalysedUnit(ms));
            }
        }
        Collections.sort(tus, new AnalysisPriorityComparator());
        return tus;
    }
    
    private Comparator<MutationDetails> comparator() {
        return new Comparator<MutationDetails>() {
            @Override
            public int compare(final MutationDetails arg0, final MutationDetails arg1) {
                return arg0.getId().compareTo(arg1.getId());
            }
        };
    }
    
    private F<ClassName, Iterable<MutationDetails>> classToMutations() {
        return new F<ClassName, Iterable<MutationDetails>>() {
            @Override
            public Iterable<MutationDetails> apply(final ClassName a) {
                return MutationTestBuilder.this.mutationSource.createMutations(a);
            }
        };
    }
    
    private MutationAnalysisUnit makePreAnalysedUnit(final List<MutationResult> analysed) {
        return new KnownStatusMutationTestUnit(analysed);
    }
    
    private MutationAnalysisUnit makeUnanalysedUnit(final Collection<MutationDetails> needAnalysis) {
        final Set<ClassName> uniqueTestClasses = new HashSet<ClassName>();
        FCollection.flatMapTo((Iterable<? extends MutationDetails>)needAnalysis, (F<MutationDetails, ? extends Iterable<Object>>)mutationDetailsToTestClass(), (Collection<? super Object>)uniqueTestClasses);
        return new MutationTestUnit(needAnalysis, uniqueTestClasses, this.workerFactory);
    }
    
    private static F<MutationResult, MutationDetails> resultToDetails() {
        return new F<MutationResult, MutationDetails>() {
            @Override
            public MutationDetails apply(final MutationResult a) {
                return a.getDetails();
            }
        };
    }
    
    private static F<MutationResult, Boolean> statusNotKnown() {
        return new F<MutationResult, Boolean>() {
            @Override
            public Boolean apply(final MutationResult a) {
                return a.getStatus() == DetectionStatus.NOT_STARTED;
            }
        };
    }
    
    private static F<MutationDetails, Iterable<ClassName>> mutationDetailsToTestClass() {
        return new F<MutationDetails, Iterable<ClassName>>() {
            @Override
            public Iterable<ClassName> apply(final MutationDetails a) {
                return FCollection.map(a.getTestsInOrder(), TestInfo.toDefiningClassName());
            }
        };
    }
}
