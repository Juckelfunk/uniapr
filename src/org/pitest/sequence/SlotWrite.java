// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.sequence;

public class SlotWrite<T>
{
    private final Slot<T> slot;
    
    public SlotWrite(final Slot<T> slot) {
        this.slot = slot;
    }
    
    Slot<T> slot() {
        return this.slot;
    }
}
