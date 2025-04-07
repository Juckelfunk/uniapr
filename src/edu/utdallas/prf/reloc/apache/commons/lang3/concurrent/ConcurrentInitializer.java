// 
// Decompiled by Procyon v0.5.36
// 

package edu.utdallas.prf.reloc.apache.commons.lang3.concurrent;

public interface ConcurrentInitializer<T>
{
    T get() throws ConcurrentException;
}
