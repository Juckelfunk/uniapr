// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.repository.metadata;

public class RepositoryMetadataReadException extends Exception
{
    public RepositoryMetadataReadException(final String message) {
        super(message);
    }
    
    public RepositoryMetadataReadException(final String message, final Exception e) {
        super(message, e);
    }
}
