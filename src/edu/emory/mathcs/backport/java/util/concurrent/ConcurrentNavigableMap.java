// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

import java.util.Set;
import edu.emory.mathcs.backport.java.util.NavigableSet;
import java.util.SortedMap;
import edu.emory.mathcs.backport.java.util.NavigableMap;

public interface ConcurrentNavigableMap extends ConcurrentMap, NavigableMap
{
    NavigableMap subMap(final Object p0, final boolean p1, final Object p2, final boolean p3);
    
    NavigableMap headMap(final Object p0, final boolean p1);
    
    NavigableMap tailMap(final Object p0, final boolean p1);
    
    SortedMap subMap(final Object p0, final Object p1);
    
    SortedMap headMap(final Object p0);
    
    SortedMap tailMap(final Object p0);
    
    NavigableMap descendingMap();
    
    NavigableSet navigableKeySet();
    
    Set keySet();
    
    NavigableSet descendingKeySet();
}
