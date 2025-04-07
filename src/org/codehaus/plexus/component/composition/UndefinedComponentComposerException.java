// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.composition;

public class UndefinedComponentComposerException extends Exception
{
    public UndefinedComponentComposerException(final String message) {
        super(message);
    }
    
    public UndefinedComponentComposerException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public UndefinedComponentComposerException(final Throwable cause) {
        super(cause);
    }
}
