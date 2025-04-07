// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon.authorization;

import org.apache.maven.wagon.WagonException;

public class AuthorizationException extends WagonException
{
    public AuthorizationException(final String message) {
        super(message);
    }
    
    public AuthorizationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
