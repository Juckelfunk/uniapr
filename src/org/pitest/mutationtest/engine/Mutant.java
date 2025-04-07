// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine;

public final class Mutant
{
    private final MutationDetails details;
    private final byte[] bytes;
    
    public Mutant(final MutationDetails details, final byte[] bytes) {
        this.details = details;
        this.bytes = bytes;
    }
    
    public MutationDetails getDetails() {
        return this.details;
    }
    
    public byte[] getBytes() {
        return this.bytes;
    }
}
