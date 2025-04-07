// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

import java.util.Map;

public interface ConcurrentMap extends Map
{
    Object putIfAbsent(final Object p0, final Object p1);
    
    boolean remove(final Object p0, final Object p1);
    
    boolean replace(final Object p0, final Object p1, final Object p2);
    
    Object replace(final Object p0, final Object p1);
}
