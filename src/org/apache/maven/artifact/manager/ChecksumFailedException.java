// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.manager;

import org.apache.maven.wagon.TransferFailedException;

public class ChecksumFailedException extends TransferFailedException
{
    public ChecksumFailedException(final String s) {
        super(s);
    }
    
    public ChecksumFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
