// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.graph;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.common.annotations.Beta;

@Beta
public interface MutableValueGraph<N, V> extends ValueGraph<N, V>
{
    @CanIgnoreReturnValue
    boolean addNode(final N p0);
    
    @CanIgnoreReturnValue
    V putEdgeValue(final N p0, final N p1, final V p2);
    
    @CanIgnoreReturnValue
    boolean removeNode(final Object p0);
    
    @CanIgnoreReturnValue
    V removeEdge(final Object p0, final Object p1);
}
