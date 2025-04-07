// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.plugin;

public abstract class AbstractMojoExecutionException extends Exception
{
    protected Object source;
    protected String longMessage;
    
    public AbstractMojoExecutionException(final String message) {
        super(message);
    }
    
    public AbstractMojoExecutionException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public String getLongMessage() {
        return this.longMessage;
    }
    
    public Object getSource() {
        return this.source;
    }
}
