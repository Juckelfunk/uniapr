// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.profiles.activation;

public class ProfileActivationException extends Exception
{
    public ProfileActivationException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public ProfileActivationException(final String message) {
        super(message);
    }
}
