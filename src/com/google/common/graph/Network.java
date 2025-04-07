// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.graph;

import javax.annotation.Nullable;
import java.util.Set;
import com.google.common.annotations.Beta;

@Beta
public interface Network<N, E>
{
    Set<N> nodes();
    
    Set<E> edges();
    
    Graph<N> asGraph();
    
    boolean isDirected();
    
    boolean allowsParallelEdges();
    
    boolean allowsSelfLoops();
    
    ElementOrder<N> nodeOrder();
    
    ElementOrder<E> edgeOrder();
    
    Set<N> adjacentNodes(final Object p0);
    
    Set<N> predecessors(final Object p0);
    
    Set<N> successors(final Object p0);
    
    Set<E> incidentEdges(final Object p0);
    
    Set<E> inEdges(final Object p0);
    
    Set<E> outEdges(final Object p0);
    
    int degree(final Object p0);
    
    int inDegree(final Object p0);
    
    int outDegree(final Object p0);
    
    EndpointPair<N> incidentNodes(final Object p0);
    
    Set<E> adjacentEdges(final Object p0);
    
    Set<E> edgesConnecting(final Object p0, final Object p1);
    
    boolean equals(@Nullable final Object p0);
    
    int hashCode();
}
