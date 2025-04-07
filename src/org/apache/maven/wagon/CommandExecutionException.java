// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon;

public class CommandExecutionException extends WagonException
{
    public CommandExecutionException(final String message) {
        super(message);
    }
    
    public CommandExecutionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
