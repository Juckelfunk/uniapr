// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon;

public class ResourceDoesNotExistException extends WagonException
{
    public ResourceDoesNotExistException(final String message) {
        super(message);
    }
    
    public ResourceDoesNotExistException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
