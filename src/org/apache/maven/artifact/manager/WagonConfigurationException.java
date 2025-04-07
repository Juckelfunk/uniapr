// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.manager;

import org.apache.maven.wagon.TransferFailedException;

public class WagonConfigurationException extends TransferFailedException
{
    static final long serialVersionUID = 1L;
    private final String originalMessage;
    private final String repositoryId;
    
    public WagonConfigurationException(final String repositoryId, final String message, final Throwable cause) {
        super("While configuring wagon for '" + repositoryId + "': " + message, cause);
        this.repositoryId = repositoryId;
        this.originalMessage = message;
    }
    
    public WagonConfigurationException(final String repositoryId, final String message) {
        super("While configuring wagon for '" + repositoryId + "': " + message);
        this.repositoryId = repositoryId;
        this.originalMessage = message;
    }
    
    public final String getRepositoryId() {
        return this.repositoryId;
    }
    
    public final String getOriginalMessage() {
        return this.originalMessage;
    }
}
