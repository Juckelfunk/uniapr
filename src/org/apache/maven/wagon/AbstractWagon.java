// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon;

import org.apache.maven.wagon.proxy.ProxyUtils;
import java.util.List;
import org.apache.maven.wagon.events.SessionListener;
import org.apache.maven.wagon.events.TransferListener;
import org.apache.maven.wagon.events.SessionEvent;
import org.apache.maven.wagon.events.TransferEvent;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.codehaus.plexus.util.IOUtil;
import java.io.OutputStream;
import java.io.InputStream;
import org.apache.maven.wagon.resource.Resource;
import java.io.IOException;
import java.io.File;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.repository.RepositoryPermissions;
import org.apache.maven.wagon.proxy.ProxyInfo;
import org.apache.maven.wagon.proxy.ProxyInfoProvider;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.apache.maven.wagon.events.TransferEventSupport;
import org.apache.maven.wagon.events.SessionEventSupport;
import org.apache.maven.wagon.repository.Repository;

public abstract class AbstractWagon implements Wagon
{
    protected static final int DEFAULT_BUFFER_SIZE = 4096;
    protected Repository repository;
    protected SessionEventSupport sessionEventSupport;
    protected TransferEventSupport transferEventSupport;
    protected AuthenticationInfo authenticationInfo;
    protected boolean interactive;
    private int connectionTimeout;
    private ProxyInfoProvider proxyInfoProvider;
    protected ProxyInfo proxyInfo;
    private RepositoryPermissions permissionsOverride;
    
    public AbstractWagon() {
        this.sessionEventSupport = new SessionEventSupport();
        this.transferEventSupport = new TransferEventSupport();
        this.interactive = true;
        this.connectionTimeout = 60000;
    }
    
    public Repository getRepository() {
        return this.repository;
    }
    
    public ProxyInfo getProxyInfo() {
        return (this.proxyInfoProvider != null) ? this.proxyInfoProvider.getProxyInfo(null) : null;
    }
    
    public AuthenticationInfo getAuthenticationInfo() {
        return this.authenticationInfo;
    }
    
    public void openConnection() throws ConnectionException, AuthenticationException {
        try {
            this.openConnectionInternal();
        }
        catch (ConnectionException e) {
            this.fireSessionConnectionRefused();
            throw e;
        }
        catch (AuthenticationException e2) {
            this.fireSessionConnectionRefused();
            throw e2;
        }
    }
    
    public void connect(final Repository repository) throws ConnectionException, AuthenticationException {
        this.connect(repository, null, (ProxyInfoProvider)null);
    }
    
    public void connect(final Repository repository, final ProxyInfo proxyInfo) throws ConnectionException, AuthenticationException {
        this.connect(repository, null, proxyInfo);
    }
    
    public void connect(final Repository repository, final ProxyInfoProvider proxyInfoProvider) throws ConnectionException, AuthenticationException {
        this.connect(repository, null, proxyInfoProvider);
    }
    
    public void connect(final Repository repository, final AuthenticationInfo authenticationInfo) throws ConnectionException, AuthenticationException {
        this.connect(repository, authenticationInfo, (ProxyInfoProvider)null);
    }
    
    public void connect(final Repository repository, final AuthenticationInfo authenticationInfo, final ProxyInfo proxyInfo) throws ConnectionException, AuthenticationException {
        final ProxyInfo proxy = proxyInfo;
        this.connect(repository, authenticationInfo, new ProxyInfoProvider() {
            public ProxyInfo getProxyInfo(final String protocol) {
                if (protocol == null || proxy == null || protocol.equalsIgnoreCase(proxy.getType())) {
                    return proxy;
                }
                return null;
            }
        });
        this.proxyInfo = proxyInfo;
    }
    
    public void connect(final Repository repository, AuthenticationInfo authenticationInfo, final ProxyInfoProvider proxyInfoProvider) throws ConnectionException, AuthenticationException {
        if (repository == null) {
            throw new IllegalStateException("The repository specified cannot be null.");
        }
        if (this.permissionsOverride != null) {
            repository.setPermissions(this.permissionsOverride);
        }
        this.repository = repository;
        if (authenticationInfo == null) {
            authenticationInfo = new AuthenticationInfo();
        }
        if (authenticationInfo.getUserName() == null && repository.getUsername() != null) {
            authenticationInfo.setUserName(repository.getUsername());
            if (repository.getPassword() != null && authenticationInfo.getPassword() == null) {
                authenticationInfo.setPassword(repository.getPassword());
            }
        }
        this.authenticationInfo = authenticationInfo;
        this.proxyInfoProvider = proxyInfoProvider;
        this.fireSessionOpening();
        this.openConnection();
        this.fireSessionOpened();
    }
    
    protected abstract void openConnectionInternal() throws ConnectionException, AuthenticationException;
    
    public void disconnect() throws ConnectionException {
        this.fireSessionDisconnecting();
        try {
            this.closeConnection();
        }
        catch (ConnectionException e) {
            this.fireSessionError(e);
            throw e;
        }
        this.fireSessionDisconnected();
    }
    
    protected abstract void closeConnection() throws ConnectionException;
    
    protected void createParentDirectories(final File destination) throws TransferFailedException {
        File destinationDirectory = destination.getParentFile();
        try {
            destinationDirectory = destinationDirectory.getCanonicalFile();
        }
        catch (IOException ex) {}
        if (destinationDirectory != null && !destinationDirectory.exists()) {
            destinationDirectory.mkdirs();
            if (!destinationDirectory.exists()) {
                throw new TransferFailedException("Specified destination directory cannot be created: " + destinationDirectory);
            }
        }
    }
    
    public void setTimeout(final int timeoutValue) {
        this.connectionTimeout = timeoutValue;
    }
    
    public int getTimeout() {
        return this.connectionTimeout;
    }
    
    protected void getTransfer(final Resource resource, final File destination, final InputStream input) throws TransferFailedException {
        this.getTransfer(resource, destination, input, true, Integer.MAX_VALUE);
    }
    
    protected void getTransfer(final Resource resource, final OutputStream output, final InputStream input) throws TransferFailedException {
        this.getTransfer(resource, output, input, true, Integer.MAX_VALUE);
    }
    
    protected void getTransfer(final Resource resource, final File destination, final InputStream input, final boolean closeInput, final int maxSize) throws TransferFailedException {
        this.fireTransferDebug("attempting to create parent directories for destination: " + destination.getName());
        this.createParentDirectories(destination);
        final OutputStream output = new LazyFileOutputStream(destination);
        this.fireGetStarted(resource, destination);
        try {
            this.getTransfer(resource, output, input, closeInput, maxSize);
        }
        catch (TransferFailedException e) {
            if (destination.exists()) {
                final boolean deleted = destination.delete();
                if (!deleted) {
                    destination.deleteOnExit();
                }
            }
            throw e;
        }
        finally {
            IOUtil.close(output);
        }
        this.fireGetCompleted(resource, destination);
    }
    
    protected void getTransfer(final Resource resource, final OutputStream output, final InputStream input, final boolean closeInput, final int maxSize) throws TransferFailedException {
        try {
            this.transfer(resource, input, output, 5, maxSize);
            this.finishGetTransfer(resource, input, output);
        }
        catch (IOException e) {
            this.fireTransferError(resource, e, 5);
            final String msg = "GET request of: " + resource.getName() + " from " + this.repository.getName() + " failed";
            throw new TransferFailedException(msg, e);
        }
        finally {
            if (closeInput) {
                IOUtil.close(input);
            }
            this.cleanupGetTransfer(resource);
        }
    }
    
    protected void finishGetTransfer(final Resource resource, final InputStream input, final OutputStream output) throws TransferFailedException {
    }
    
    protected void cleanupGetTransfer(final Resource resource) {
    }
    
    protected void putTransfer(final Resource resource, final File source, final OutputStream output, final boolean closeOutput) throws TransferFailedException, AuthorizationException, ResourceDoesNotExistException {
        this.firePutStarted(resource, source);
        this.transfer(resource, source, output, closeOutput);
        this.firePutCompleted(resource, source);
    }
    
    protected void transfer(final Resource resource, final File source, final OutputStream output, final boolean closeOutput) throws TransferFailedException, AuthorizationException, ResourceDoesNotExistException {
        InputStream input = null;
        try {
            input = new FileInputStream(source);
            this.putTransfer(resource, input, output, closeOutput);
        }
        catch (FileNotFoundException e) {
            this.fireTransferError(resource, e, 6);
            throw new TransferFailedException("Specified source file does not exist: " + source, e);
        }
        finally {
            IOUtil.close(input);
        }
    }
    
    protected void putTransfer(final Resource resource, final InputStream input, final OutputStream output, final boolean closeOutput) throws TransferFailedException, AuthorizationException, ResourceDoesNotExistException {
        try {
            this.transfer(resource, input, output, 6);
            this.finishPutTransfer(resource, input, output);
        }
        catch (IOException e) {
            this.fireTransferError(resource, e, 6);
            final String msg = "PUT request to: " + resource.getName() + " in " + this.repository.getName() + " failed";
            throw new TransferFailedException(msg, e);
        }
        finally {
            if (closeOutput) {
                IOUtil.close(output);
            }
            this.cleanupPutTransfer(resource);
        }
    }
    
    protected void cleanupPutTransfer(final Resource resource) {
    }
    
    protected void finishPutTransfer(final Resource resource, final InputStream input, final OutputStream output) throws TransferFailedException, AuthorizationException, ResourceDoesNotExistException {
    }
    
    protected void transfer(final Resource resource, final InputStream input, final OutputStream output, final int requestType) throws IOException {
        this.transfer(resource, input, output, requestType, Integer.MAX_VALUE);
    }
    
    protected void transfer(final Resource resource, final InputStream input, final OutputStream output, final int requestType, final int maxSize) throws IOException {
        final byte[] buffer = new byte[4096];
        final TransferEvent transferEvent = new TransferEvent(this, resource, 3, requestType);
        transferEvent.setTimestamp(System.currentTimeMillis());
        int n;
        for (int remaining = maxSize; remaining > 0; remaining -= n) {
            n = input.read(buffer, 0, Math.min(buffer.length, remaining));
            if (n == -1) {
                break;
            }
            this.fireTransferProgress(transferEvent, buffer, n);
            output.write(buffer, 0, n);
        }
        output.flush();
    }
    
    protected void fireTransferProgress(final TransferEvent transferEvent, final byte[] buffer, final int n) {
        this.transferEventSupport.fireTransferProgress(transferEvent, buffer, n);
    }
    
    protected void fireGetCompleted(final Resource resource, final File localFile) {
        final long timestamp = System.currentTimeMillis();
        final TransferEvent transferEvent = new TransferEvent(this, resource, 2, 5);
        transferEvent.setTimestamp(timestamp);
        transferEvent.setLocalFile(localFile);
        this.transferEventSupport.fireTransferCompleted(transferEvent);
    }
    
    protected void fireGetStarted(final Resource resource, final File localFile) {
        final long timestamp = System.currentTimeMillis();
        final TransferEvent transferEvent = new TransferEvent(this, resource, 1, 5);
        transferEvent.setTimestamp(timestamp);
        transferEvent.setLocalFile(localFile);
        this.transferEventSupport.fireTransferStarted(transferEvent);
    }
    
    protected void fireGetInitiated(final Resource resource, final File localFile) {
        final long timestamp = System.currentTimeMillis();
        final TransferEvent transferEvent = new TransferEvent(this, resource, 0, 5);
        transferEvent.setTimestamp(timestamp);
        transferEvent.setLocalFile(localFile);
        this.transferEventSupport.fireTransferInitiated(transferEvent);
    }
    
    protected void firePutInitiated(final Resource resource, final File localFile) {
        final long timestamp = System.currentTimeMillis();
        final TransferEvent transferEvent = new TransferEvent(this, resource, 0, 6);
        transferEvent.setTimestamp(timestamp);
        transferEvent.setLocalFile(localFile);
        this.transferEventSupport.fireTransferInitiated(transferEvent);
    }
    
    protected void firePutCompleted(final Resource resource, final File localFile) {
        final long timestamp = System.currentTimeMillis();
        final TransferEvent transferEvent = new TransferEvent(this, resource, 2, 6);
        transferEvent.setTimestamp(timestamp);
        transferEvent.setLocalFile(localFile);
        this.transferEventSupport.fireTransferCompleted(transferEvent);
    }
    
    protected void firePutStarted(final Resource resource, final File localFile) {
        final long timestamp = System.currentTimeMillis();
        final TransferEvent transferEvent = new TransferEvent(this, resource, 1, 6);
        transferEvent.setTimestamp(timestamp);
        transferEvent.setLocalFile(localFile);
        this.transferEventSupport.fireTransferStarted(transferEvent);
    }
    
    protected void fireSessionDisconnected() {
        final long timestamp = System.currentTimeMillis();
        final SessionEvent sessionEvent = new SessionEvent(this, 3);
        sessionEvent.setTimestamp(timestamp);
        this.sessionEventSupport.fireSessionDisconnected(sessionEvent);
    }
    
    protected void fireSessionDisconnecting() {
        final long timestamp = System.currentTimeMillis();
        final SessionEvent sessionEvent = new SessionEvent(this, 2);
        sessionEvent.setTimestamp(timestamp);
        this.sessionEventSupport.fireSessionDisconnecting(sessionEvent);
    }
    
    protected void fireSessionLoggedIn() {
        final long timestamp = System.currentTimeMillis();
        final SessionEvent sessionEvent = new SessionEvent(this, 7);
        sessionEvent.setTimestamp(timestamp);
        this.sessionEventSupport.fireSessionLoggedIn(sessionEvent);
    }
    
    protected void fireSessionLoggedOff() {
        final long timestamp = System.currentTimeMillis();
        final SessionEvent sessionEvent = new SessionEvent(this, 8);
        sessionEvent.setTimestamp(timestamp);
        this.sessionEventSupport.fireSessionLoggedOff(sessionEvent);
    }
    
    protected void fireSessionOpened() {
        final long timestamp = System.currentTimeMillis();
        final SessionEvent sessionEvent = new SessionEvent(this, 6);
        sessionEvent.setTimestamp(timestamp);
        this.sessionEventSupport.fireSessionOpened(sessionEvent);
    }
    
    protected void fireSessionOpening() {
        final long timestamp = System.currentTimeMillis();
        final SessionEvent sessionEvent = new SessionEvent(this, 5);
        sessionEvent.setTimestamp(timestamp);
        this.sessionEventSupport.fireSessionOpening(sessionEvent);
    }
    
    protected void fireSessionConnectionRefused() {
        final long timestamp = System.currentTimeMillis();
        final SessionEvent sessionEvent = new SessionEvent(this, 4);
        sessionEvent.setTimestamp(timestamp);
        this.sessionEventSupport.fireSessionConnectionRefused(sessionEvent);
    }
    
    protected void fireSessionError(final Exception exception) {
        final long timestamp = System.currentTimeMillis();
        final SessionEvent sessionEvent = new SessionEvent(this, exception);
        sessionEvent.setTimestamp(timestamp);
        this.sessionEventSupport.fireSessionError(sessionEvent);
    }
    
    protected void fireTransferDebug(final String message) {
        this.transferEventSupport.fireDebug(message);
    }
    
    protected void fireSessionDebug(final String message) {
        this.sessionEventSupport.fireDebug(message);
    }
    
    public boolean hasTransferListener(final TransferListener listener) {
        return this.transferEventSupport.hasTransferListener(listener);
    }
    
    public void addTransferListener(final TransferListener listener) {
        this.transferEventSupport.addTransferListener(listener);
    }
    
    public void removeTransferListener(final TransferListener listener) {
        this.transferEventSupport.removeTransferListener(listener);
    }
    
    public void addSessionListener(final SessionListener listener) {
        this.sessionEventSupport.addSessionListener(listener);
    }
    
    public boolean hasSessionListener(final SessionListener listener) {
        return this.sessionEventSupport.hasSessionListener(listener);
    }
    
    public void removeSessionListener(final SessionListener listener) {
        this.sessionEventSupport.removeSessionListener(listener);
    }
    
    protected void fireTransferError(final Resource resource, final Exception e, final int requestType) {
        final TransferEvent transferEvent = new TransferEvent(this, resource, e, requestType);
        this.transferEventSupport.fireTransferError(transferEvent);
    }
    
    public SessionEventSupport getSessionEventSupport() {
        return this.sessionEventSupport;
    }
    
    public void setSessionEventSupport(final SessionEventSupport sessionEventSupport) {
        this.sessionEventSupport = sessionEventSupport;
    }
    
    public TransferEventSupport getTransferEventSupport() {
        return this.transferEventSupport;
    }
    
    public void setTransferEventSupport(final TransferEventSupport transferEventSupport) {
        this.transferEventSupport = transferEventSupport;
    }
    
    protected void postProcessListeners(final Resource resource, final File source, final int requestType) throws TransferFailedException {
        final byte[] buffer = new byte[4096];
        final TransferEvent transferEvent = new TransferEvent(this, resource, 3, requestType);
        transferEvent.setTimestamp(System.currentTimeMillis());
        transferEvent.setLocalFile(source);
        InputStream input = null;
        try {
            input = new FileInputStream(source);
            while (true) {
                final int n = input.read(buffer);
                if (n == -1) {
                    break;
                }
                this.fireTransferProgress(transferEvent, buffer, n);
            }
        }
        catch (IOException e) {
            this.fireTransferError(resource, e, requestType);
            throw new TransferFailedException("Failed to post-process the source file", e);
        }
        finally {
            IOUtil.close(input);
        }
    }
    
    public void putDirectory(final File sourceDirectory, final String destinationDirectory) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        throw new UnsupportedOperationException("The wagon you are using has not implemented putDirectory()");
    }
    
    public boolean supportsDirectoryCopy() {
        return false;
    }
    
    protected static String getPath(final String basedir, final String dir) {
        String path = basedir;
        if (!basedir.endsWith("/") && !dir.startsWith("/")) {
            path += "/";
        }
        path += dir;
        return path;
    }
    
    public boolean isInteractive() {
        return this.interactive;
    }
    
    public void setInteractive(final boolean interactive) {
        this.interactive = interactive;
    }
    
    public List getFileList(final String destinationDirectory) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        throw new UnsupportedOperationException("The wagon you are using has not implemented getFileList()");
    }
    
    public boolean resourceExists(final String resourceName) throws TransferFailedException, AuthorizationException {
        throw new UnsupportedOperationException("The wagon you are using has not implemented resourceExists()");
    }
    
    protected ProxyInfo getProxyInfo(final String protocol, final String host) {
        if (this.proxyInfoProvider != null) {
            final ProxyInfo proxyInfo = this.proxyInfoProvider.getProxyInfo(protocol);
            if (!ProxyUtils.validateNonProxyHosts(proxyInfo, host)) {
                return proxyInfo;
            }
        }
        return null;
    }
    
    public RepositoryPermissions getPermissionsOverride() {
        return this.permissionsOverride;
    }
    
    public void setPermissionsOverride(final RepositoryPermissions permissionsOverride) {
        this.permissionsOverride = permissionsOverride;
    }
}
