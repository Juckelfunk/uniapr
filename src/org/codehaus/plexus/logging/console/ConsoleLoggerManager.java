// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.logging.console;

import org.codehaus.plexus.logging.Logger;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.logging.AbstractLoggerManager;

public class ConsoleLoggerManager extends AbstractLoggerManager implements LoggerManager, Initializable
{
    private String threshold;
    private int currentThreshold;
    private Map loggers;
    private int loggerCount;
    private boolean bootTimeLogger;
    
    public ConsoleLoggerManager() {
        this.threshold = "info";
        this.bootTimeLogger = false;
    }
    
    public ConsoleLoggerManager(final String threshold) {
        this.threshold = "info";
        this.bootTimeLogger = false;
        this.threshold = threshold;
        this.bootTimeLogger = true;
        this.initialize();
    }
    
    public void initialize() {
        this.debug("Initializing ConsoleLoggerManager: " + this.hashCode() + ".");
        this.currentThreshold = this.parseThreshold(this.threshold);
        if (this.currentThreshold == -1) {
            this.debug("Could not parse the threshold level: '" + this.threshold + "', setting to debug.");
            this.currentThreshold = 0;
        }
        this.loggers = new HashMap();
    }
    
    public void setThreshold(final int currentThreshold) {
        this.currentThreshold = currentThreshold;
    }
    
    public int getThreshold() {
        return this.currentThreshold;
    }
    
    public void setThreshold(final String role, final String roleHint, final int threshold) {
        final String name = this.toMapKey(role, roleHint);
        final ConsoleLogger logger = this.loggers.get(name);
        if (logger == null) {
            this.debug("Trying to set the threshold of a unknown logger '" + name + "'.");
            return;
        }
        logger.setThreshold(threshold);
    }
    
    public int getThreshold(final String role, final String roleHint) {
        final String name = this.toMapKey(role, roleHint);
        final ConsoleLogger logger = this.loggers.get(name);
        if (logger == null) {
            this.debug("Trying to get the threshold of a unknown logger '" + name + "'.");
            return 0;
        }
        return logger.getThreshold();
    }
    
    public Logger getLoggerForComponent(final String role, final String roleHint) {
        final String name = this.toMapKey(role, roleHint);
        Logger logger = this.loggers.get(name);
        if (logger != null) {
            return logger;
        }
        this.debug("Creating logger '" + name + "' " + this.hashCode() + ".");
        logger = new ConsoleLogger(this.getThreshold(), name);
        this.loggers.put(name, logger);
        return logger;
    }
    
    public void returnComponentLogger(final String role, final String roleHint) {
        final String name = this.toMapKey(role, roleHint);
        final Object obj = this.loggers.remove(name);
        if (obj == null) {
            this.debug("There was no such logger '" + name + "' " + this.hashCode() + ".");
        }
        else {
            this.debug("Removed logger '" + name + "' " + this.hashCode() + ".");
        }
    }
    
    public int getActiveLoggerCount() {
        return this.loggers.size();
    }
    
    private int parseThreshold(String text) {
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
    
    private String decodeLogLevel(final int logLevel) {
        switch (logLevel) {
            case 0: {
                return "debug";
            }
            case 1: {
                return "info";
            }
            case 2: {
                return "warn";
            }
            case 3: {
                return "error";
            }
            case 4: {
                return "fatal";
            }
            case 5: {
                return "disabled";
            }
            default: {
                return "unknown";
            }
        }
    }
    
    private void debug(final String msg) {
    }
}
