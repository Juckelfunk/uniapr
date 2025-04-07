// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.repository.metadata;

public class RepositoryMetadataStoreException extends Exception
{
    public RepositoryMetadataStoreException(final String message) {
        super(message);
    }
    
    public RepositoryMetadataStoreException(final String message, final Exception e) {
        super(message, e);
    }
}
