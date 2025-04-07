// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.context;

public interface Context
{
    Object get(final Object p0) throws ContextException;
    
    boolean contains(final Object p0);
    
    void put(final Object p0, final Object p1) throws IllegalStateException;
    
    void hide(final Object p0) throws IllegalStateException;
    
    void makeReadOnly();
}
