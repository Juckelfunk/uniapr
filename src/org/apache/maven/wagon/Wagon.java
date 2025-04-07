// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon;

import org.apache.maven.wagon.events.TransferListener;
import org.apache.maven.wagon.events.SessionListener;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.apache.maven.wagon.proxy.ProxyInfoProvider;
import org.apache.maven.wagon.proxy.ProxyInfo;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.repository.Repository;
import java.util.List;
import org.apache.maven.wagon.authorization.AuthorizationException;
import java.io.File;

public interface Wagon
{
    public static final String ROLE = ((Wagon$1.class$org$apache$maven$wagon$Wagon == null) ? (Wagon$1.class$org$apache$maven$wagon$Wagon = Wagon$1.class$("org.apache.maven.wagon.Wagon")) : Wagon$1.class$org$apache$maven$wagon$Wagon).getName();
    
    void get(final String p0, final File p1) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException;
    
    boolean getIfNewer(final String p0, final File p1, final long p2) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException;
    
    void put(final File p0, final String p1) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException;
    
    void putDirectory(final File p0, final String p1) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException;
    
    boolean resourceExists(final String p0) throws TransferFailedException, AuthorizationException;
    
    List getFileList(final String p0) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException;
    
    boolean supportsDirectoryCopy();
    
    Repository getRepository();
    
    void connect(final Repository p0) throws ConnectionException, AuthenticationException;
    
    void connect(final Repository p0, final ProxyInfo p1) throws ConnectionException, AuthenticationException;
    
    void connect(final Repository p0, final ProxyInfoProvider p1) throws ConnectionException, AuthenticationException;
    
    void connect(final Repository p0, final AuthenticationInfo p1) throws ConnectionException, AuthenticationException;
    
    void connect(final Repository p0, final AuthenticationInfo p1, final ProxyInfo p2) throws ConnectionException, AuthenticationException;
    
    void connect(final Repository p0, final AuthenticationInfo p1, final ProxyInfoProvider p2) throws ConnectionException, AuthenticationException;
    
    void openConnection() throws ConnectionException, AuthenticationException;
    
    void disconnect() throws ConnectionException;
    
    void setTimeout(final int p0);
    
    int getTimeout();
    
    void addSessionListener(final SessionListener p0);
    
    void removeSessionListener(final SessionListener p0);
    
    boolean hasSessionListener(final SessionListener p0);
    
    void addTransferListener(final TransferListener p0);
    
    void removeTransferListener(final TransferListener p0);
    
    boolean hasTransferListener(final TransferListener p0);
    
    boolean isInteractive();
    
    void setInteractive(final boolean p0);
}
