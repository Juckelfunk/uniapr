// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon;

import java.io.OutputStream;
import org.codehaus.plexus.util.IOUtil;
import org.apache.maven.wagon.resource.Resource;
import java.io.InputStream;
import java.io.File;
import org.apache.maven.wagon.authorization.AuthorizationException;

public abstract class StreamWagon extends AbstractWagon implements StreamingWagon
{
    public abstract void fillInputData(final InputData p0) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException;
    
    public abstract void fillOutputData(final OutputData p0) throws TransferFailedException;
    
    public abstract void closeConnection() throws ConnectionException;
    
    public void get(final String resourceName, final File destination) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        this.getIfNewer(resourceName, destination, 0L);
    }
    
    protected void checkInputStream(final InputStream is, final Resource resource) throws TransferFailedException {
        if (is == null) {
            final TransferFailedException e = new TransferFailedException(this.getRepository().getUrl() + " - Could not open input stream for resource: '" + resource + "'");
            this.fireTransferError(resource, e, 5);
            throw e;
        }
    }
    
    public boolean getIfNewer(final String resourceName, final File destination, final long timestamp) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        boolean retValue = false;
        final Resource resource = new Resource(resourceName);
        this.fireGetInitiated(resource, destination);
        resource.setLastModified(timestamp);
        final InputStream is = this.getInputStream(resource);
        if (timestamp == 0L || timestamp < resource.getLastModified()) {
            retValue = true;
            this.checkInputStream(is, resource);
            this.getTransfer(resource, destination, is);
        }
        else {
            IOUtil.close(is);
        }
        return retValue;
    }
    
    protected InputStream getInputStream(final Resource resource) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        final InputData inputData = new InputData();
        inputData.setResource(resource);
        try {
            this.fillInputData(inputData);
        }
        catch (TransferFailedException e) {
            this.fireTransferError(resource, e, 5);
            this.cleanupGetTransfer(resource);
            throw e;
        }
        catch (ResourceDoesNotExistException e2) {
            this.fireTransferError(resource, e2, 5);
            this.cleanupGetTransfer(resource);
            throw e2;
        }
        catch (AuthorizationException e3) {
            this.fireTransferError(resource, e3, 5);
            this.cleanupGetTransfer(resource);
            throw e3;
        }
        finally {
            if (inputData.getInputStream() == null) {
                this.cleanupGetTransfer(resource);
            }
        }
        return inputData.getInputStream();
    }
    
    public void put(final File source, final String resourceName) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        final Resource resource = new Resource(resourceName);
        this.firePutInitiated(resource, source);
        resource.setContentLength(source.length());
        resource.setLastModified(source.lastModified());
        final OutputStream os = this.getOutputStream(resource);
        this.checkOutputStream(resource, os);
        this.putTransfer(resource, source, os, true);
    }
    
    protected void checkOutputStream(final Resource resource, final OutputStream os) throws TransferFailedException {
        if (os == null) {
            final TransferFailedException e = new TransferFailedException(this.getRepository().getUrl() + " - Could not open output stream for resource: '" + resource + "'");
            this.fireTransferError(resource, e, 6);
            throw e;
        }
    }
    
    protected OutputStream getOutputStream(final Resource resource) throws TransferFailedException {
        final OutputData outputData = new OutputData();
        outputData.setResource(resource);
        try {
            this.fillOutputData(outputData);
        }
        catch (TransferFailedException e) {
            this.fireTransferError(resource, e, 6);
            throw e;
        }
        finally {
            if (outputData.getOutputStream() == null) {
                this.cleanupPutTransfer(resource);
            }
        }
        return outputData.getOutputStream();
    }
    
    public boolean getIfNewerToStream(final String resourceName, final OutputStream stream, final long timestamp) throws ResourceDoesNotExistException, TransferFailedException, AuthorizationException {
        boolean retValue = false;
        final Resource resource = new Resource(resourceName);
        this.fireGetInitiated(resource, null);
        final InputStream is = this.getInputStream(resource);
        if (timestamp == 0L || timestamp < resource.getLastModified()) {
            retValue = true;
            this.checkInputStream(is, resource);
            this.fireGetStarted(resource, null);
            this.getTransfer(resource, stream, is, true, Integer.MAX_VALUE);
            this.fireGetCompleted(resource, null);
        }
        else {
            IOUtil.close(is);
        }
        return retValue;
    }
    
    public void getToStream(final String resourceName, final OutputStream stream) throws ResourceDoesNotExistException, TransferFailedException, AuthorizationException {
        this.getIfNewerToStream(resourceName, stream, 0L);
    }
    
    public void putFromStream(final InputStream stream, final String destination) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        final Resource resource = new Resource(destination);
        this.firePutInitiated(resource, null);
        this.putFromStream(stream, resource);
    }
    
    public void putFromStream(final InputStream stream, final String destination, final long contentLength, final long lastModified) throws TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        final Resource resource = new Resource(destination);
        this.firePutInitiated(resource, null);
        resource.setContentLength(contentLength);
        resource.setLastModified(lastModified);
        this.putFromStream(stream, resource);
    }
    
    private void putFromStream(final InputStream stream, final Resource resource) throws TransferFailedException, AuthorizationException, ResourceDoesNotExistException {
        final OutputStream os = this.getOutputStream(resource);
        this.checkOutputStream(resource, os);
        this.firePutStarted(resource, null);
        this.putTransfer(resource, stream, os, true);
        this.firePutCompleted(resource, null);
    }
}
