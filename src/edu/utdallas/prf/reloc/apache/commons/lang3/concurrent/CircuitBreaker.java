// 
// Decompiled by Procyon v0.5.36
// 

package edu.utdallas.prf.reloc.apache.commons.lang3.concurrent;

public interface CircuitBreaker<T>
{
    boolean isOpen();
    
    boolean isClosed();
    
    boolean checkState();
    
    void close();
    
    void open();
    
    boolean incrementAndCheckState(final T p0);
}
