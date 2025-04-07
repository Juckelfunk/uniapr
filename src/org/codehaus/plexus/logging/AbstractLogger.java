// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.logging;

public abstract class AbstractLogger implements Logger
{
    private int threshold;
    private String name;
    
    public AbstractLogger(final int threshold, final String name) {
        if (!this.isValidThreshold(threshold)) {
            throw new IllegalArgumentException("Threshold " + threshold + " is not valid");
        }
        this.threshold = threshold;
        this.name = name;
    }
    
    public int getThreshold() {
        return this.threshold;
    }
    
    public void setThreshold(final int threshold) {
        this.threshold = threshold;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void debug(final String message) {
        this.debug(message, null);
    }
    
    public boolean isDebugEnabled() {
        return this.threshold <= 0;
    }
    
    public void info(final String message) {
        this.info(message, null);
    }
    
    public boolean isInfoEnabled() {
        return this.threshold <= 1;
    }
    
    public void warn(final String message) {
        this.warn(message, null);
    }
    
    public boolean isWarnEnabled() {
        return this.threshold <= 2;
    }
    
    public void error(final String message) {
        this.error(message, null);
    }
    
    public boolean isErrorEnabled() {
        return this.threshold <= 3;
    }
    
    public void fatalError(final String message) {
        this.fatalError(message, null);
    }
    
    public boolean isFatalErrorEnabled() {
        return this.threshold <= 4;
    }
    
    protected boolean isValidThreshold(final int threshold) {
        return threshold == 0 || threshold == 1 || threshold == 2 || threshold == 3 || threshold == 4 || threshold == 5;
    }
}
