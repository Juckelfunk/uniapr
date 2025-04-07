// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.factory;

public class ComponentInstantiationException extends Exception
{
    public ComponentInstantiationException(final String message) {
        super(message);
    }
    
    public ComponentInstantiationException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public ComponentInstantiationException(final Throwable cause) {
        super(cause);
    }
}
