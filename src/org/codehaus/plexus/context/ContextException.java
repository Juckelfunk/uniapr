// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.context;

public class ContextException extends Exception
{
    public ContextException(final String message) {
        this(message, null);
    }
    
    public ContextException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
