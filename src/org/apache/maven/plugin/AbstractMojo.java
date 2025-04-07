// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.plugin;

import org.apache.maven.plugin.logging.SystemStreamLog;
import java.util.Map;
import org.apache.maven.plugin.logging.Log;

public abstract class AbstractMojo implements Mojo, ContextEnabled
{
    private Log log;
    private Map pluginContext;
    
    public void setLog(final Log log) {
        this.log = log;
    }
    
    public Log getLog() {
        if (this.log == null) {
            this.log = new SystemStreamLog();
        }
        return this.log;
    }
    
    public Map getPluginContext() {
        return this.pluginContext;
    }
    
    public void setPluginContext(final Map pluginContext) {
        this.pluginContext = pluginContext;
    }
}
