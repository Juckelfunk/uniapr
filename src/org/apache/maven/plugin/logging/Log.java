// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.plugin.logging;

public interface Log
{
    boolean isDebugEnabled();
    
    void debug(final CharSequence p0);
    
    void debug(final CharSequence p0, final Throwable p1);
    
    void debug(final Throwable p0);
    
    boolean isInfoEnabled();
    
    void info(final CharSequence p0);
    
    void info(final CharSequence p0, final Throwable p1);
    
    void info(final Throwable p0);
    
    boolean isWarnEnabled();
    
    void warn(final CharSequence p0);
    
    void warn(final CharSequence p0, final Throwable p1);
    
    void warn(final Throwable p0);
    
    boolean isErrorEnabled();
    
    void error(final CharSequence p0);
    
    void error(final CharSequence p0, final Throwable p1);
    
    void error(final Throwable p0);
}
