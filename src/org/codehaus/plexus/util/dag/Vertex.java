// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.dag;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Vertex implements Cloneable, Serializable
{
    private String label;
    List children;
    List parents;
    
    public Vertex(final String label) {
        this.label = null;
        this.children = new ArrayList();
        this.parents = new ArrayList();
        this.label = label;
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public void addEdgeTo(final Vertex vertex) {
        this.children.add(vertex);
    }
    
    public void removeEdgeTo(final Vertex vertex) {
        this.children.remove(vertex);
    }
    
    public void addEdgeFrom(final Vertex vertex) {
        this.parents.add(vertex);
    }
    
    public void removeEdgeFrom(final Vertex vertex) {
        this.parents.remove(vertex);
    }
    
    public List getChildren() {
        return this.children;
    }
    
    public List getChildLabels() {
        final List retValue = new ArrayList(this.children.size());
        for (final Vertex vertex : this.children) {
            retValue.add(vertex.getLabel());
        }
        return retValue;
    }
    
    public List getParents() {
        return this.parents;
    }
    
    public List getParentLabels() {
        final List retValue = new ArrayList(this.parents.size());
        for (final Vertex vertex : this.parents) {
            retValue.add(vertex.getLabel());
        }
        return retValue;
    }
    
    public boolean isLeaf() {
        return this.children.size() == 0;
    }
    
    public boolean isRoot() {
        return this.parents.size() == 0;
    }
    
    public boolean isConnected() {
        return this.isRoot() || this.isLeaf();
    }
    
    public Object clone() throws CloneNotSupportedException {
        final Object retValue = super.clone();
        return retValue;
    }
    
    public String toString() {
        return "Vertex{label='" + this.label + "'" + "}";
    }
}
