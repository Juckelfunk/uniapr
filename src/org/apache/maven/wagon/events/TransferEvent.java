// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon.events;

import org.apache.maven.wagon.Wagon;
import java.io.File;
import org.apache.maven.wagon.resource.Resource;

public class TransferEvent extends WagonEvent
{
    public static final int TRANSFER_INITIATED = 0;
    public static final int TRANSFER_STARTED = 1;
    public static final int TRANSFER_COMPLETED = 2;
    public static final int TRANSFER_PROGRESS = 3;
    public static final int TRANSFER_ERROR = 4;
    public static final int REQUEST_GET = 5;
    public static final int REQUEST_PUT = 6;
    private Resource resource;
    private int eventType;
    private int requestType;
    private Exception exception;
    private File localFile;
    
    public TransferEvent(final Wagon wagon, final Resource resource, final int eventType, final int requestType) {
        super(wagon);
        this.resource = resource;
        this.setEventType(eventType);
        this.setRequestType(requestType);
    }
    
    public TransferEvent(final Wagon wagon, final Resource resource, final Exception exception, final int requestType) {
        this(wagon, resource, 4, requestType);
        this.exception = exception;
    }
    
    public Resource getResource() {
        return this.resource;
    }
    
    public Exception getException() {
        return this.exception;
    }
    
    public int getRequestType() {
        return this.requestType;
    }
    
    public void setRequestType(final int requestType) {
        switch (requestType) {
            case 6: {
                break;
            }
            case 5: {
                break;
            }
            default: {
                throw new IllegalArgumentException("Illegal request type: " + requestType);
            }
        }
        this.requestType = requestType;
    }
    
    public int getEventType() {
        return this.eventType;
    }
    
    public void setEventType(final int eventType) {
        switch (eventType) {
            case 0: {
                break;
            }
            case 1: {
                break;
            }
            case 2: {
                break;
            }
            case 3: {
                break;
            }
            case 4: {
                break;
            }
            default: {
                throw new IllegalArgumentException("Illegal event type: " + eventType);
            }
        }
        this.eventType = eventType;
    }
    
    public void setResource(final Resource resource) {
        this.resource = resource;
    }
    
    public File getLocalFile() {
        return this.localFile;
    }
    
    public void setLocalFile(final File localFile) {
        this.localFile = localFile;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("TransferEvent[");
        switch (this.getRequestType()) {
            case 5: {
                sb.append("GET");
                break;
            }
            case 6: {
                sb.append("PUT");
                break;
            }
            default: {
                sb.append(this.getRequestType());
                break;
            }
        }
        sb.append("|");
        switch (this.getEventType()) {
            case 2: {
                sb.append("COMPLETED");
                break;
            }
            case 4: {
                sb.append("ERROR");
                break;
            }
            case 0: {
                sb.append("INITIATED");
                break;
            }
            case 3: {
                sb.append("PROGRESS");
                break;
            }
            case 1: {
                sb.append("STARTED");
                break;
            }
            default: {
                sb.append(this.getEventType());
                break;
            }
        }
        sb.append("|");
        sb.append(this.getWagon().getRepository()).append("|");
        sb.append(this.getLocalFile()).append("|");
        sb.append(this.getResource().inspect());
        sb.append("]");
        return sb.toString();
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + this.eventType;
        result = 31 * result + ((this.exception == null) ? 0 : this.exception.hashCode());
        result = 31 * result + ((this.localFile == null) ? 0 : this.localFile.hashCode());
        result = 31 * result + this.requestType;
        result = 31 * result + ((this.resource == null) ? 0 : this.resource.hashCode());
        return result;
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        final TransferEvent other = (TransferEvent)obj;
        if (this.eventType != other.eventType) {
            return false;
        }
        if (this.exception == null) {
            if (other.exception != null) {
                return false;
            }
        }
        else if (!this.exception.getClass().equals(other.exception.getClass())) {
            return false;
        }
        if (this.requestType != other.requestType) {
            return false;
        }
        if (this.resource == null) {
            if (other.resource != null) {
                return false;
            }
        }
        else {
            if (!this.resource.equals(other.resource)) {
                return false;
            }
            if (!this.source.equals(other.source)) {
                return false;
            }
        }
        return true;
    }
}
