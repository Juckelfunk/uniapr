// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.installer;

public class ArtifactInstallationException extends Exception
{
    public ArtifactInstallationException(final String message) {
        super(message);
    }
    
    public ArtifactInstallationException(final Throwable cause) {
        super(cause);
    }
    
    public ArtifactInstallationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
