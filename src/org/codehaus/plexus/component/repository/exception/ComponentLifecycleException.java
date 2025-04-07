// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.repository.exception;

public class ComponentLifecycleException extends Exception
{
    public ComponentLifecycleException(final String message) {
        super(message);
    }
    
    public ComponentLifecycleException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
