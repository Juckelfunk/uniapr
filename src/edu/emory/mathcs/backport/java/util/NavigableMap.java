// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util;

import java.util.Map;
import java.util.SortedMap;

public interface NavigableMap extends SortedMap
{
    Map.Entry lowerEntry(final Object p0);
    
    Object lowerKey(final Object p0);
    
    Map.Entry floorEntry(final Object p0);
    
    Object floorKey(final Object p0);
    
    Map.Entry ceilingEntry(final Object p0);
    
    Object ceilingKey(final Object p0);
    
    Map.Entry higherEntry(final Object p0);
    
    Object higherKey(final Object p0);
    
    Map.Entry firstEntry();
    
    Map.Entry lastEntry();
    
    Map.Entry pollFirstEntry();
    
    Map.Entry pollLastEntry();
    
    NavigableMap descendingMap();
    
    NavigableSet navigableKeySet();
    
    NavigableSet descendingKeySet();
    
    NavigableMap subMap(final Object p0, final boolean p1, final Object p2, final boolean p3);
    
    NavigableMap headMap(final Object p0, final boolean p1);
    
    NavigableMap tailMap(final Object p0, final boolean p1);
    
    SortedMap subMap(final Object p0, final Object p1);
    
    SortedMap headMap(final Object p0);
    
    SortedMap tailMap(final Object p0);
}
