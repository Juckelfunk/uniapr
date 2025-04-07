// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

public class TimeoutException extends Exception
{
    private static final long serialVersionUID = 1900926677490660714L;
    
    public TimeoutException() {
    }
    
    public TimeoutException(final String message) {
        super(message);
    }
}
