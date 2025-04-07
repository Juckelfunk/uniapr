// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

public class ExecutionException extends Exception
{
    private static final long serialVersionUID = 7830266012832686185L;
    
    protected ExecutionException() {
    }
    
    protected ExecutionException(final String message) {
        super(message);
    }
    
    public ExecutionException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public ExecutionException(final Throwable cause) {
        super(cause);
    }
}
