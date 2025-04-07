// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.blocks;

public class ConcreteBlockCounter implements BlockCounter
{
    private int currentBlock;
    private boolean isWithinExceptionHandler;
    
    public ConcreteBlockCounter() {
        this.currentBlock = 0;
    }
    
    @Override
    public void registerNewBlock() {
        ++this.currentBlock;
    }
    
    @Override
    public void registerFinallyBlockStart() {
        this.isWithinExceptionHandler = true;
    }
    
    @Override
    public void registerFinallyBlockEnd() {
        this.isWithinExceptionHandler = false;
    }
    
    public int getCurrentBlock() {
        return this.currentBlock;
    }
    
    public boolean isWithinFinallyBlock() {
        return this.isWithinExceptionHandler;
    }
}
