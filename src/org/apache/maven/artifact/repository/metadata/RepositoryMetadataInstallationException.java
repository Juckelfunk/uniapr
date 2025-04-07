// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.repository.metadata;

public class RepositoryMetadataInstallationException extends Throwable
{
    public RepositoryMetadataInstallationException(final String message) {
        super(message);
    }
    
    public RepositoryMetadataInstallationException(final String message, final Exception e) {
        super(message, e);
    }
}
