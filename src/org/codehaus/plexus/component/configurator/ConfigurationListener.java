// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator;

public interface ConfigurationListener
{
    void notifyFieldChangeUsingSetter(final String p0, final Object p1, final Object p2);
    
    void notifyFieldChangeUsingReflection(final String p0, final Object p1, final Object p2);
}
