// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon;

public class UnsupportedProtocolException extends WagonException
{
    public UnsupportedProtocolException(final String message) {
        super(message);
    }
    
    public UnsupportedProtocolException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
