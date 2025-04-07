// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.factory;

public class UndefinedComponentFactoryException extends Exception
{
    public UndefinedComponentFactoryException(final String message) {
        super(message);
    }
    
    public UndefinedComponentFactoryException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public UndefinedComponentFactoryException(final Throwable cause) {
        super(cause);
    }
}
