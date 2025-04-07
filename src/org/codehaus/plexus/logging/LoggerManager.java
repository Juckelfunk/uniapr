// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.logging;

public interface LoggerManager
{
    public static final String ROLE = ((LoggerManager$1.class$org$codehaus$plexus$logging$LoggerManager == null) ? (LoggerManager$1.class$org$codehaus$plexus$logging$LoggerManager = LoggerManager$1.class$("org.codehaus.plexus.logging.LoggerManager")) : LoggerManager$1.class$org$codehaus$plexus$logging$LoggerManager).getName();
    
    void setThreshold(final int p0);
    
    int getThreshold();
    
    void setThreshold(final String p0, final int p1);
    
    void setThreshold(final String p0, final String p1, final int p2);
    
    int getThreshold(final String p0);
    
    int getThreshold(final String p0, final String p1);
    
    Logger getLoggerForComponent(final String p0);
    
    Logger getLoggerForComponent(final String p0, final String p1);
    
    void returnComponentLogger(final String p0);
    
    void returnComponentLogger(final String p0, final String p1);
    
    int getActiveLoggerCount();
}
