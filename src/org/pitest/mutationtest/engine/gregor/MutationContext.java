// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor;

import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.mutationtest.engine.gregor.blocks.BlockCounter;

public interface MutationContext extends BlockCounter
{
    void registerCurrentLine(final int p0);
    
    ClassInfo getClassInfo();
    
    MutationIdentifier registerMutation(final MethodMutatorFactory p0, final String p1);
    
    boolean shouldMutate(final MutationIdentifier p0);
    
    void disableMutations(final String p0);
    
    void enableMutatations(final String p0);
}
