// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.repository.metadata;

public class RepositoryMetadataDeploymentException extends Throwable
{
    public RepositoryMetadataDeploymentException(final String message) {
        super(message);
    }
    
    public RepositoryMetadataDeploymentException(final String message, final Exception e) {
        super(message, e);
    }
}
