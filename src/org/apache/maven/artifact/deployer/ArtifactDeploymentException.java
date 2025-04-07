// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.deployer;

public class ArtifactDeploymentException extends Exception
{
    public ArtifactDeploymentException(final String message) {
        super(message);
    }
    
    public ArtifactDeploymentException(final Throwable cause) {
        super(cause);
    }
    
    public ArtifactDeploymentException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
