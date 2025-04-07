// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.logging.console;

import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.AbstractLogger;

public final class ConsoleLogger extends AbstractLogger
{
    public ConsoleLogger(final int threshold, final String name) {
        super(threshold, name);
    }
    
    public void debug(final String message, final Throwable throwable) {
        if (this.isDebugEnabled()) {
            System.out.print("[DEBUG] ");
            System.out.println(message);
            if (null != throwable) {
                throwable.printStackTrace(System.out);
            }
        }
    }
    
    public void info(final String message, final Throwable throwable) {
        if (this.isInfoEnabled()) {
            System.out.print("[INFO] ");
            System.out.println(message);
            if (null != throwable) {
                throwable.printStackTrace(System.out);
            }
        }
    }
    
    public void warn(final String message, final Throwable throwable) {
        if (this.isWarnEnabled()) {
            System.out.print("[WARNING] ");
            System.out.println(message);
            if (null != throwable) {
                throwable.printStackTrace(System.out);
            }
        }
    }
    
    public void error(final String message, final Throwable throwable) {
        if (this.isErrorEnabled()) {
            System.out.print("[ERROR] ");
            System.out.println(message);
            if (null != throwable) {
                throwable.printStackTrace(System.out);
            }
        }
    }
    
    public void fatalError(final String message, final Throwable throwable) {
        if (this.isFatalErrorEnabled()) {
            System.out.print("[FATAL ERROR] ");
            System.out.println(message);
            if (null != throwable) {
                throwable.printStackTrace(System.out);
            }
        }
    }
    
    public Logger getChildLogger(final String name) {
        return this;
    }
}
