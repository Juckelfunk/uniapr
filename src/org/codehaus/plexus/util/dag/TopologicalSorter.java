// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.dag;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class TopologicalSorter
{
    private static final Integer NOT_VISTITED;
    private static final Integer VISITING;
    private static final Integer VISITED;
    
    public static List sort(final DAG graph) {
        return dfs(graph);
    }
    
    public static List sort(final Vertex vertex) {
        final LinkedList retValue = new LinkedList();
        final Map vertexStateMap = new HashMap();
        dfsVisit(vertex, vertexStateMap, retValue);
        return retValue;
    }
    
    private static List dfs(final DAG graph) {
        final List verticies = graph.getVerticies();
        final LinkedList retValue = new LinkedList();
        final Map vertexStateMap = new HashMap();
        for (final Vertex vertex : verticies) {
            if (isNotVisited(vertex, vertexStateMap)) {
                dfsVisit(vertex, vertexStateMap, retValue);
            }
        }
        return retValue;
    }
    
    private static boolean isNotVisited(final Vertex vertex, final Map vertexStateMap) {
        if (!vertexStateMap.containsKey(vertex)) {
            return true;
        }
        final Integer state = vertexStateMap.get(vertex);
        return TopologicalSorter.NOT_VISTITED.equals(state);
    }
    
    private static void dfsVisit(final Vertex vertex, final Map vertexStateMap, final LinkedList list) {
        vertexStateMap.put(vertex, TopologicalSorter.VISITING);
        final List verticies = vertex.getChildren();
        for (final Vertex v : verticies) {
            if (isNotVisited(v, vertexStateMap)) {
                dfsVisit(v, vertexStateMap, list);
            }
        }
        vertexStateMap.put(vertex, TopologicalSorter.VISITED);
        list.add(vertex.getLabel());
    }
    
    static {
        NOT_VISTITED = new Integer(0);
        VISITING = new Integer(1);
        VISITED = new Integer(2);
    }
}
