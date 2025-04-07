// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.analysis;

public interface InstructionCounter
{
    void increment();
    
    int currentInstructionCount();
}
