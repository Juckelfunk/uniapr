// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.plugin;

import java.util.Map;

public interface ContextEnabled
{
    void setPluginContext(final Map p0);
    
    Map getPluginContext();
}
