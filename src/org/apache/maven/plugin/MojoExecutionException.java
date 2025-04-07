// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.plugin;

public class MojoExecutionException extends AbstractMojoExecutionException
{
    public MojoExecutionException(final Object source, final String shortMessage, final String longMessage) {
        super(shortMessage);
        this.source = source;
        this.longMessage = longMessage;
    }
    
    public MojoExecutionException(final String message, final Exception cause) {
        super(message, cause);
    }
    
    public MojoExecutionException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public MojoExecutionException(final String message) {
        super(message);
    }
}
