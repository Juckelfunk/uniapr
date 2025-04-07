// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.blocks;

public interface BlockCounter
{
    void registerNewBlock();
    
    void registerFinallyBlockStart();
    
    void registerFinallyBlockEnd();
}
