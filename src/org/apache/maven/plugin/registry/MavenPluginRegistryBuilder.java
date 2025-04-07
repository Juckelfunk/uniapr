// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.plugin.registry;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import java.io.IOException;

public interface MavenPluginRegistryBuilder
{
    public static final String ROLE = MavenPluginRegistryBuilder.class.getName();
    public static final String ALT_USER_PLUGIN_REG_LOCATION = "org.apache.maven.user-plugin-registry";
    public static final String ALT_GLOBAL_PLUGIN_REG_LOCATION = "org.apache.maven.global-plugin-registry";
    
    PluginRegistry buildPluginRegistry() throws IOException, XmlPullParserException;
    
    PluginRegistry createUserPluginRegistry();
}
