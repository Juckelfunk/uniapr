// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.logging;

public abstract class AbstractLogEnabled implements LogEnabled
{
    private Logger logger;
    
    public void enableLogging(final Logger logger) {
        this.logger = logger;
    }
    
    protected Logger getLogger() {
        return this.logger;
    }
    
    protected void setupLogger(final Object component) {
        this.setupLogger(component, this.logger);
    }
    
    protected void setupLogger(final Object component, final String subCategory) {
        if (subCategory == null) {
            throw new IllegalStateException("Logging category must be defined.");
        }
        final Logger logger = this.logger.getChildLogger(subCategory);
        this.setupLogger(component, logger);
    }
    
    protected void setupLogger(final Object component, final Logger logger) {
        if (component instanceof LogEnabled) {
            ((LogEnabled)component).enableLogging(logger);
        }
    }
}
