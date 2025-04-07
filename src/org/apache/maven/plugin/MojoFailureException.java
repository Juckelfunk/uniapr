// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.plugin;

public class MojoFailureException extends AbstractMojoExecutionException
{
    public MojoFailureException(final Object source, final String shortMessage, final String longMessage) {
        super(shortMessage);
        this.source = source;
        this.longMessage = longMessage;
    }
    
    public MojoFailureException(final String message) {
        super(message);
    }
    
    public MojoFailureException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
