// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.logging;

public interface Logger
{
    public static final int LEVEL_DEBUG = 0;
    public static final int LEVEL_INFO = 1;
    public static final int LEVEL_WARN = 2;
    public static final int LEVEL_ERROR = 3;
    public static final int LEVEL_FATAL = 4;
    public static final int LEVEL_DISABLED = 5;
    
    void debug(final String p0);
    
    void debug(final String p0, final Throwable p1);
    
    boolean isDebugEnabled();
    
    void info(final String p0);
    
    void info(final String p0, final Throwable p1);
    
    boolean isInfoEnabled();
    
    void warn(final String p0);
    
    void warn(final String p0, final Throwable p1);
    
    boolean isWarnEnabled();
    
    void error(final String p0);
    
    void error(final String p0, final Throwable p1);
    
    boolean isErrorEnabled();
    
    void fatalError(final String p0);
    
    void fatalError(final String p0, final Throwable p1);
    
    boolean isFatalErrorEnabled();
    
    Logger getChildLogger(final String p0);
    
    int getThreshold();
    
    String getName();
}
