// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.cli;

public class CommandLineTimeOutException extends CommandLineException
{
    public CommandLineTimeOutException(final String message) {
        super(message);
    }
    
    public CommandLineTimeOutException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
