// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.logging;

public abstract class AbstractLoggerManager implements LoggerManager
{
    public void setThreshold(final String role, final int threshold) {
        this.setThreshold(role, null, threshold);
    }
    
    public int getThreshold(final String role) {
        return this.getThreshold(role, null);
    }
    
    public Logger getLoggerForComponent(final String role) {
        return this.getLoggerForComponent(role, null);
    }
    
    public void returnComponentLogger(final String role) {
        this.returnComponentLogger(role, null);
    }
    
    protected String toMapKey(final String role, final String roleHint) {
        if (roleHint == null) {
            return role;
        }
        return role + ":" + roleHint;
    }
}
