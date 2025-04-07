// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.logging;

import java.util.HashMap;
import java.util.Map;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

public abstract class BaseLoggerManager extends AbstractLoggerManager implements Initializable
{
    private Map loggerCache;
    private String threshold;
    private int currentThreshold;
    
    public BaseLoggerManager() {
        this.loggerCache = new HashMap();
        this.threshold = "info";
    }
    
    public void initialize() {
        this.currentThreshold = this.parseThreshold(this.threshold);
        if (this.currentThreshold == -1) {
            this.currentThreshold = 0;
        }
    }
    
    protected int parseThreshold(String text) {
        text = text.trim().toLowerCase();
        if (text.equals("debug")) {
            return 0;
        }
        if (text.equals("info")) {
            return 1;
        }
        if (text.equals("warn")) {
            return 2;
        }
        if (text.equals("error")) {
            return 3;
        }
        if (text.equals("fatal")) {
            return 4;
        }
        return -1;
    }
    
    public void setThreshold(final int currentThreshold) {
        this.currentThreshold = currentThreshold;
    }
    
    public int getThreshold() {
        return this.currentThreshold;
    }
    
    public void setThreshold(final String role, final String roleHint, final int threshold) {
        final String key = this.toMapKey(role, roleHint);
        final AbstractLogger logger = this.loggerCache.get(key);
        if (logger == null) {
            return;
        }
        logger.setThreshold(threshold);
    }
    
    public int getThreshold(final String role, final String roleHint) {
        final String key = this.toMapKey(role, roleHint);
        final AbstractLogger logger = this.loggerCache.get(key);
        if (logger == null) {
            return 0;
        }
        return logger.getThreshold();
    }
    
    public Logger getLoggerForComponent(final String role, final String roleHint) {
        final String key = this.toMapKey(role, roleHint);
        Logger logger = this.loggerCache.get(key);
        if (logger != null) {
            return logger;
        }
        logger = this.createLogger(key);
        this.loggerCache.put(key, logger);
        return logger;
    }
    
    protected abstract Logger createLogger(final String p0);
    
    public void returnComponentLogger(final String role, final String roleHint) {
        final String key = this.toMapKey(role, roleHint);
        final Object obj = this.loggerCache.remove(key);
        if (obj == null) {
            System.err.println("There was no such logger '" + key + "' " + this.hashCode() + ".");
        }
    }
    
    public int getActiveLoggerCount() {
        return this.loggerCache.size();
    }
    
    public String getThresholdAsString() {
        return this.threshold;
    }
}
