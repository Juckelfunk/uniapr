// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.repository.metadata;

public class RepositoryMetadataResolutionException extends Exception
{
    public RepositoryMetadataResolutionException(final String message) {
        super(message);
    }
    
    public RepositoryMetadataResolutionException(final String message, final Exception e) {
        super(message, e);
    }
}
