// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest;

public interface MutationResultListener
{
    void runStart();
    
    void handleMutationResult(final ClassMutationResults p0);
    
    void runEnd();
}
