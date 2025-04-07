// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.personality.plexus;

import org.codehaus.plexus.lifecycle.AbstractLifecycleHandler;

public class PlexusLifecycleHandler extends AbstractLifecycleHandler
{
    public static String COMPONENT_CONFIGURATOR;
    
    public void initialize() {
    }
    
    static {
        PlexusLifecycleHandler.COMPONENT_CONFIGURATOR = "component.configurator";
    }
}
