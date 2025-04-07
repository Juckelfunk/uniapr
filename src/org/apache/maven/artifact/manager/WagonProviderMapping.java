// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.manager;

public interface WagonProviderMapping
{
    public static final String ROLE = WagonProviderMapping.class.getName();
    
    void setWagonProvider(final String p0, final String p1);
    
    String getWagonProvider(final String p0);
}
