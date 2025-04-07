// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.analysis;

public class DefaultInstructionCounter implements InstructionCounter
{
    private int count;
    
    @Override
    public void increment() {
        ++this.count;
    }
    
    @Override
    public int currentInstructionCount() {
        return this.count;
    }
}
