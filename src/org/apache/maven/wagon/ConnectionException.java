// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon;

public class ConnectionException extends WagonException
{
    public ConnectionException(final String message) {
        super(message);
    }
    
    public ConnectionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
