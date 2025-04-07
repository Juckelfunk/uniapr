// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon;

import java.io.InputStream;
import org.apache.maven.wagon.authorization.AuthorizationException;
import java.io.OutputStream;

public interface StreamingWagon extends Wagon
{
    void getToStream(final String p0, final OutputStream p1) throws ResourceDoesNotExistException, TransferFailedException, AuthorizationException;
    
    boolean getIfNewerToStream(final String p0, final OutputStream p1, final long p2) throws ResourceDoesNotExistException, TransferFailedException, AuthorizationException;
    
    void putFromStream(final InputStream p0, final String p1) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException;
    
    void putFromStream(final InputStream p0, final String p1, final long p2, final long p3) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException;
}
