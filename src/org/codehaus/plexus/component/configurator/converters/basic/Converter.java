// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator.converters.basic;

public interface Converter
{
    boolean canConvert(final Class p0);
    
    Object fromString(final String p0);
    
    String toString(final Object p0);
}
