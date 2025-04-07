// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.dag;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class CycleDetector
{
    private static final Integer NOT_VISTITED;
    private static final Integer VISITING;
    private static final Integer VISITED;
    
    public static List hasCycle(final DAG graph) {
        final List verticies = graph.getVerticies();
        final Map vertexStateMap = new HashMap();
        List retValue = null;
        for (final Vertex vertex : verticies) {
            if (isNotVisited(vertex, vertexStateMap)) {
                retValue = introducesCycle(vertex, vertexStateMap);
                if (retValue != null) {
                    break;
                }
                continue;
            }
        }
        return retValue;
    }
    
    public static List introducesCycle(final Vertex vertex, final Map vertexStateMap) {
        final LinkedList cycleStack = new LinkedList();
        final boolean hasCycle = dfsVisit(vertex, cycleStack, vertexStateMap);
        if (hasCycle) {
            final String label = cycleStack.getFirst();
            final int pos = cycleStack.lastIndexOf(label);
            final List cycle = cycleStack.subList(0, pos + 1);
            Collections.reverse(cycle);
            return cycle;
        }
        return null;
    }
    
    public static List introducesCycle(final Vertex vertex) {
        final Map vertexStateMap = new HashMap();
        return introducesCycle(vertex, vertexStateMap);
    }
    
    private static boolean isNotVisited(final Vertex vertex, final Map vertexStateMap) {
        if (!vertexStateMap.containsKey(vertex)) {
            return true;
        }
        final Integer state = vertexStateMap.get(vertex);
        return CycleDetector.NOT_VISTITED.equals(state);
    }
    
    private static boolean isVisiting(final Vertex vertex, final Map vertexStateMap) {
        final Integer state = vertexStateMap.get(vertex);
        return CycleDetector.VISITING.equals(state);
    }
    
    private static boolean dfsVisit(final Vertex vertex, final LinkedList cycle, final Map vertexStateMap) {
        cycle.addFirst(vertex.getLabel());
        vertexStateMap.put(vertex, CycleDetector.VISITING);
        final List verticies = vertex.getChildren();
        for (final Vertex v : verticies) {
            if (isNotVisited(v, vertexStateMap)) {
                final boolean hasCycle = dfsVisit(v, cycle, vertexStateMap);
                if (hasCycle) {
                    return true;
                }
                continue;
            }
            else {
                if (isVisiting(v, vertexStateMap)) {
                    cycle.addFirst(v.getLabel());
                    return true;
                }
                continue;
            }
        }
        vertexStateMap.put(vertex, CycleDetector.VISITED);
        cycle.removeFirst();
        return false;
    }
    
    static {
        NOT_VISTITED = new Integer(0);
        VISITING = new Integer(1);
        VISITED = new Integer(2);
    }
}
