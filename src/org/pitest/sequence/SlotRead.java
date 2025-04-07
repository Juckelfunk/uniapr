// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.sequence;

public class SlotRead<T>
{
    private final Slot<T> slot;
    
    public SlotRead(final Slot<T> slot) {
        this.slot = slot;
    }
    
    Slot<T> slot() {
        return this.slot;
    }
}
