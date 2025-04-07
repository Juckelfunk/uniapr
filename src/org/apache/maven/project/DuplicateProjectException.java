// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project;

public class DuplicateProjectException extends Exception
{
    public DuplicateProjectException(final String message) {
        super(message);
    }
    
    public DuplicateProjectException(final String message, final Exception e) {
        super(message, e);
    }
}
