// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.config;

import org.pitest.mutationtest.ClassMutationResults;
import java.util.Iterator;
import org.pitest.mutationtest.MutationResultListener;

public class CompoundTestListener implements MutationResultListener
{
    private final Iterable<MutationResultListener> children;
    
    public CompoundTestListener(final Iterable<MutationResultListener> children) {
        this.children = children;
    }
    
    @Override
    public void runStart() {
        for (final MutationResultListener each : this.children) {
            each.runStart();
        }
    }
    
    @Override
    public void handleMutationResult(final ClassMutationResults metaData) {
        for (final MutationResultListener each : this.children) {
            each.handleMutationResult(metaData);
        }
    }
    
    @Override
    public void runEnd() {
        for (final MutationResultListener each : this.children) {
            each.runEnd();
        }
    }
}
