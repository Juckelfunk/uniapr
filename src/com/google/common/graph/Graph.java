// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.graph;

import javax.annotation.Nullable;
import java.util.Set;
import com.google.common.annotations.Beta;

@Beta
public interface Graph<N>
{
    Set<N> nodes();
    
    Set<EndpointPair<N>> edges();
    
    boolean isDirected();
    
    boolean allowsSelfLoops();
    
    ElementOrder<N> nodeOrder();
    
    Set<N> adjacentNodes(final Object p0);
    
    Set<N> predecessors(final Object p0);
    
    Set<N> successors(final Object p0);
    
    int degree(final Object p0);
    
    int inDegree(final Object p0);
    
    int outDegree(final Object p0);
    
    boolean equals(@Nullable final Object p0);
    
    int hashCode();
}
