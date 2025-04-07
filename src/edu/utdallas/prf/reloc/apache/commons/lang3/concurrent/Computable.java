// 
// Decompiled by Procyon v0.5.36
// 

package edu.utdallas.prf.reloc.apache.commons.lang3.concurrent;

public interface Computable<I, O>
{
    O compute(final I p0) throws InterruptedException;
}
