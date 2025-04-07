// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.coverage.analysis;

import java.util.Set;

public final class Block
{
    private final int firstInstruction;
    private final int lastInstruction;
    private final Set<Integer> lines;
    
    public Block(final int firstInstruction, final int lastInstruction, final Set<Integer> lines) {
        this.firstInstruction = firstInstruction;
        this.lastInstruction = lastInstruction;
        this.lines = lines;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + this.firstInstruction;
        result = 31 * result + this.lastInstruction;
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Block other = (Block)obj;
        return this.firstInstruction == other.firstInstruction && this.lastInstruction == other.lastInstruction;
    }
    
    @Override
    public String toString() {
        return "Block [firstInstruction=" + this.firstInstruction + ", lastInstruction=" + this.lastInstruction + "]";
    }
    
    public boolean firstInstructionIs(final int ins) {
        return this.firstInstruction == ins;
    }
    
    public Set<Integer> getLines() {
        return this.lines;
    }
    
    public int getFirstInstruction() {
        return this.firstInstruction;
    }
    
    public int getLastInstruction() {
        return this.lastInstruction;
    }
}
