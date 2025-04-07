// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project;

public class MissingProjectException extends Exception
{
    public MissingProjectException(final String message) {
        super(message);
    }
    
    public MissingProjectException(final String message, final Exception e) {
        super(message, e);
    }
}
