// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

public class RejectedExecutionException extends RuntimeException
{
    private static final long serialVersionUID = -375805702767069545L;
    
    public RejectedExecutionException() {
    }
    
    public RejectedExecutionException(final String message) {
        super(message);
    }
    
    public RejectedExecutionException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public RejectedExecutionException(final Throwable cause) {
        super(cause);
    }
}
