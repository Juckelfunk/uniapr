// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest;

import org.pitest.functional.predicate.Predicate;
import java.util.Set;
import org.pitest.functional.F;
import java.util.List;
import org.pitest.functional.SideEffect1;
import org.pitest.functional.FCollection;
import org.pitest.functional.prelude.Prelude;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import org.pitest.mutationtest.engine.MutationDetails;
import java.util.Map;

public class MutationStatusMap
{
    private final Map<MutationDetails, MutationStatusTestPair> mutationMap;
    
    public MutationStatusMap() {
        this.mutationMap = new HashMap<MutationDetails, MutationStatusTestPair>();
    }
    
    public void setStatusForMutation(final MutationDetails mutation, final DetectionStatus status) {
        this.setStatusForMutations(Collections.singleton(mutation), status);
    }
    
    public void setStatusForMutation(final MutationDetails mutation, final MutationStatusTestPair status) {
        this.mutationMap.put(mutation, status);
    }
    
    public void setStatusForMutations(final Collection<MutationDetails> mutations, final DetectionStatus status) {
        FCollection.forEach(mutations, (SideEffect1<Object>)Prelude.putToMap((Map<A, MutationStatusTestPair>)this.mutationMap, new MutationStatusTestPair(0, status)));
    }
    
    public List<MutationResult> createMutationResults() {
        return FCollection.map(this.mutationMap.entrySet(), detailsToMutationResults());
    }
    
    public boolean hasUnrunMutations() {
        return !this.getUnrunMutations().isEmpty();
    }
    
    public Collection<MutationDetails> getUnrunMutations() {
        return FCollection.filter(this.mutationMap.entrySet(), (F<Map.Entry<MutationDetails, MutationStatusTestPair>, Boolean>)hasStatus(DetectionStatus.NOT_STARTED)).map(toMutationDetails());
    }
    
    public Collection<MutationDetails> getUnfinishedRuns() {
        return FCollection.filter(this.mutationMap.entrySet(), (F<Map.Entry<MutationDetails, MutationStatusTestPair>, Boolean>)hasStatus(DetectionStatus.STARTED)).map(toMutationDetails());
    }
    
    public Set<MutationDetails> allMutations() {
        return this.mutationMap.keySet();
    }
    
    private static F<Map.Entry<MutationDetails, MutationStatusTestPair>, MutationResult> detailsToMutationResults() {
        return new F<Map.Entry<MutationDetails, MutationStatusTestPair>, MutationResult>() {
            @Override
            public MutationResult apply(final Map.Entry<MutationDetails, MutationStatusTestPair> a) {
                return new MutationResult(a.getKey(), a.getValue());
            }
        };
    }
    
    private static F<Map.Entry<MutationDetails, MutationStatusTestPair>, MutationDetails> toMutationDetails() {
        return new F<Map.Entry<MutationDetails, MutationStatusTestPair>, MutationDetails>() {
            @Override
            public MutationDetails apply(final Map.Entry<MutationDetails, MutationStatusTestPair> a) {
                return a.getKey();
            }
        };
    }
    
    private static Predicate<Map.Entry<MutationDetails, MutationStatusTestPair>> hasStatus(final DetectionStatus status) {
        return new Predicate<Map.Entry<MutationDetails, MutationStatusTestPair>>() {
            @Override
            public Boolean apply(final Map.Entry<MutationDetails, MutationStatusTestPair> a) {
                return a.getValue().getStatus().equals(status);
            }
        };
    }
    
    public void markUncoveredMutations() {
        this.setStatusForMutations(FCollection.filter(this.mutationMap.keySet(), hasNoCoverage()), DetectionStatus.NO_COVERAGE);
    }
    
    private static F<MutationDetails, Boolean> hasNoCoverage() {
        return new F<MutationDetails, Boolean>() {
            @Override
            public Boolean apply(final MutationDetails a) {
                return a.getTestsInOrder().isEmpty();
            }
        };
    }
}
