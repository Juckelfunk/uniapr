// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.reflection;

public class ReflectorException extends Exception
{
    public ReflectorException() {
    }
    
    public ReflectorException(final String msg) {
        super(msg);
    }
    
    public ReflectorException(final Throwable root) {
        super(root);
    }
    
    public ReflectorException(final String msg, final Throwable root) {
        super(msg, root);
    }
}
