// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon.authentication;

import org.apache.maven.wagon.WagonException;

public class AuthenticationException extends WagonException
{
    public AuthenticationException(final String message) {
        super(message);
    }
    
    public AuthenticationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
