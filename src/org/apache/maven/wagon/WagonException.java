// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon;

public abstract class WagonException extends Exception
{
    private Throwable cause;
    
    public WagonException(final String message, final Throwable cause) {
        super(message);
        this.initCause(cause);
    }
    
    public WagonException(final String message) {
        super(message);
    }
    
    public Throwable getCause() {
        return this.cause;
    }
    
    public Throwable initCause(final Throwable cause) {
        this.cause = cause;
        return this;
    }
}
