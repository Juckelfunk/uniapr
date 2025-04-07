// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.statistics;

import org.pitest.functional.F2;
import org.pitest.functional.F;
import org.pitest.functional.SideEffect1;
import org.pitest.functional.FCollection;
import org.pitest.mutationtest.MutationResult;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class MutationStatisticsPrecursor
{
    private final Map<String, ScorePrecursor> mutatorTotalMap;
    private long numberOfTestsRun;
    
    MutationStatisticsPrecursor() {
        this.mutatorTotalMap = new HashMap<String, ScorePrecursor>();
        this.numberOfTestsRun = 0L;
    }
    
    public void registerResults(final Collection<MutationResult> results) {
        FCollection.forEach(results, this.register());
    }
    
    private SideEffect1<MutationResult> register() {
        return new SideEffect1<MutationResult>() {
            @Override
            public void apply(final MutationResult mr) {
                MutationStatisticsPrecursor.this.numberOfTestsRun += mr.getNumberOfTestsRun();
                final String key = mr.getDetails().getId().getMutator();
                ScorePrecursor total = MutationStatisticsPrecursor.this.mutatorTotalMap.get(key);
                if (total == null) {
                    total = new ScorePrecursor(key);
                    MutationStatisticsPrecursor.this.mutatorTotalMap.put(key, total);
                }
                total.registerResult(mr.getStatus());
            }
        };
    }
    
    public MutationStatistics toStatistics() {
        final Iterable<Score> scores = this.getScores();
        final long totalMutations = FCollection.fold(addTotals(), 0L, scores);
        final long totalDetected = FCollection.fold(addDetectedTotals(), 0L, scores);
        return new MutationStatistics(scores, totalMutations, totalDetected, this.numberOfTestsRun);
    }
    
    Iterable<Score> getScores() {
        return FCollection.map(this.mutatorTotalMap.values(), toScore());
    }
    
    private static F<ScorePrecursor, Score> toScore() {
        return new F<ScorePrecursor, Score>() {
            @Override
            public Score apply(final ScorePrecursor a) {
                return a.toScore();
            }
        };
    }
    
    private static F2<Long, Score, Long> addTotals() {
        return new F2<Long, Score, Long>() {
            @Override
            public Long apply(final Long a, final Score b) {
                return a + b.getTotalMutations();
            }
        };
    }
    
    private static F2<Long, Score, Long> addDetectedTotals() {
        return new F2<Long, Score, Long>() {
            @Override
            public Long apply(final Long a, final Score b) {
                return a + b.getTotalDetectedMutations();
            }
        };
    }
}
