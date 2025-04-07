// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.graph;

import javax.annotation.Nullable;
import com.google.common.annotations.Beta;

@Beta
public interface ValueGraph<N, V> extends Graph<N>
{
    V edgeValue(final Object p0, final Object p1);
    
    V edgeValueOrDefault(final Object p0, final Object p1, @Nullable final V p2);
    
    boolean equals(@Nullable final Object p0);
    
    int hashCode();
}
