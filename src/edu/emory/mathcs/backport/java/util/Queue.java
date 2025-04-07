// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util;

import java.util.Collection;

public interface Queue extends Collection
{
    boolean add(final Object p0);
    
    boolean offer(final Object p0);
    
    Object remove();
    
    Object poll();
    
    Object element();
    
    Object peek();
}
