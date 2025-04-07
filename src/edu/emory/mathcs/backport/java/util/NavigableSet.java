// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util;

import java.util.Iterator;
import java.util.SortedSet;

public interface NavigableSet extends SortedSet
{
    Object lower(final Object p0);
    
    Object floor(final Object p0);
    
    Object ceiling(final Object p0);
    
    Object higher(final Object p0);
    
    Object pollFirst();
    
    Object pollLast();
    
    Iterator iterator();
    
    NavigableSet descendingSet();
    
    Iterator descendingIterator();
    
    NavigableSet subSet(final Object p0, final boolean p1, final Object p2, final boolean p3);
    
    NavigableSet headSet(final Object p0, final boolean p1);
    
    NavigableSet tailSet(final Object p0, final boolean p1);
    
    SortedSet subSet(final Object p0, final Object p1);
    
    SortedSet headSet(final Object p0);
    
    SortedSet tailSet(final Object p0);
}
