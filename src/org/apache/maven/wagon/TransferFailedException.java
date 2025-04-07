// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon;

public class TransferFailedException extends WagonException
{
    public TransferFailedException(final String message) {
        super(message);
    }
    
    public TransferFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
