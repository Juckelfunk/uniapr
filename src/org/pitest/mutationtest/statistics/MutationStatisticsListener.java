// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.statistics;

import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.MutationResultListener;

public class MutationStatisticsListener implements MutationResultListener, MutationStatisticsSource
{
    private final MutationStatisticsPrecursor mutatorScores;
    
    public MutationStatisticsListener() {
        this.mutatorScores = new MutationStatisticsPrecursor();
    }
    
    @Override
    public MutationStatistics getStatistics() {
        return this.mutatorScores.toStatistics();
    }
    
    @Override
    public void runStart() {
    }
    
    @Override
    public void handleMutationResult(final ClassMutationResults metaData) {
        this.processMetaData(metaData);
    }
    
    @Override
    public void runEnd() {
    }
    
    private void processMetaData(final ClassMutationResults value) {
        this.mutatorScores.registerResults(value.getMutations());
    }
}
