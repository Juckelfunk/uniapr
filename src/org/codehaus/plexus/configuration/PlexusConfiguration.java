// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.configuration;

public interface PlexusConfiguration
{
    String getName();
    
    String getValue() throws PlexusConfigurationException;
    
    String getValue(final String p0);
    
    String[] getAttributeNames();
    
    String getAttribute(final String p0) throws PlexusConfigurationException;
    
    String getAttribute(final String p0, final String p1);
    
    PlexusConfiguration getChild(final String p0);
    
    PlexusConfiguration getChild(final int p0);
    
    PlexusConfiguration getChild(final String p0, final boolean p1);
    
    PlexusConfiguration[] getChildren();
    
    PlexusConfiguration[] getChildren(final String p0);
    
    void addChild(final PlexusConfiguration p0);
    
    int getChildCount();
}
