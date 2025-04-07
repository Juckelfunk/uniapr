// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr;

import java.io.Serializable;

public class PatchId implements Serializable
{
    private static final long serialVersionUID = 1L;
    private static int idCounter;
    private final int id;
    
    private PatchId(final int id) {
        this.id = id;
    }
    
    public static PatchId alloc() {
        return new PatchId(PatchId.idCounter++);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final PatchId that = (PatchId)o;
        return this.id == that.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.valueOf(this.id).hashCode();
    }
    
    static {
        PatchId.idCounter = 0;
    }
}
