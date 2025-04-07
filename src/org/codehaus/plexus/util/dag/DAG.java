// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.dag;

import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

public class DAG implements Cloneable, Serializable
{
    private Map vertexMap;
    private List vertexList;
    
    public DAG() {
        this.vertexMap = new HashMap();
        this.vertexList = new ArrayList();
    }
    
    public List getVerticies() {
        return this.vertexList;
    }
    
    public Set getLabels() {
        final Set retValue = this.vertexMap.keySet();
        return retValue;
    }
    
    public Vertex addVertex(final String label) {
        Vertex retValue = null;
        if (this.vertexMap.containsKey(label)) {
            retValue = this.vertexMap.get(label);
        }
        else {
            retValue = new Vertex(label);
            this.vertexMap.put(label, retValue);
            this.vertexList.add(retValue);
        }
        return retValue;
    }
    
    public void addEdge(final String from, final String to) throws CycleDetectedException {
        final Vertex v1 = this.addVertex(from);
        final Vertex v2 = this.addVertex(to);
        this.addEdge(v1, v2);
    }
    
    public void addEdge(final Vertex from, final Vertex to) throws CycleDetectedException {
        from.addEdgeTo(to);
        to.addEdgeFrom(from);
        final List cycle = CycleDetector.introducesCycle(to);
        if (cycle != null) {
            this.removeEdge(from, to);
            final String msg = "Edge between '" + from + "' and '" + to + "' introduces to cycle in the graph";
            throw new CycleDetectedException(msg, cycle);
        }
    }
    
    public void removeEdge(final String from, final String to) {
        final Vertex v1 = this.addVertex(from);
        final Vertex v2 = this.addVertex(to);
        this.removeEdge(v1, v2);
    }
    
    public void removeEdge(final Vertex from, final Vertex to) {
        from.removeEdgeTo(to);
        to.removeEdgeFrom(from);
    }
    
    public Vertex getVertex(final String label) {
        final Vertex retValue = this.vertexMap.get(label);
        return retValue;
    }
    
    public boolean hasEdge(final String label1, final String label2) {
        final Vertex v1 = this.getVertex(label1);
        final Vertex v2 = this.getVertex(label2);
        final boolean retValue = v1.getChildren().contains(v2);
        return retValue;
    }
    
    public List getChildLabels(final String label) {
        final Vertex vertex = this.getVertex(label);
        return vertex.getChildLabels();
    }
    
    public List getParentLabels(final String label) {
        final Vertex vertex = this.getVertex(label);
        return vertex.getParentLabels();
    }
    
    public Object clone() throws CloneNotSupportedException {
        final Object retValue = super.clone();
        return retValue;
    }
    
    public boolean isConnected(final String label) {
        final Vertex vertex = this.getVertex(label);
        final boolean retValue = vertex.isConnected();
        return retValue;
    }
    
    public List getSuccessorLabels(final String label) {
        final Vertex vertex = this.getVertex(label);
        List retValue;
        if (vertex.isLeaf()) {
            retValue = new ArrayList(1);
            retValue.add(label);
        }
        else {
            retValue = TopologicalSorter.sort(vertex);
        }
        return retValue;
    }
}
