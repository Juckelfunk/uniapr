// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon.resource;

public class Resource
{
    private String name;
    private long lastModified;
    private long contentLength;
    
    public Resource() {
        this.contentLength = -1L;
    }
    
    public Resource(final String name) {
        this.contentLength = -1L;
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public long getLastModified() {
        return this.lastModified;
    }
    
    public void setLastModified(final long lastModified) {
        this.lastModified = lastModified;
    }
    
    public long getContentLength() {
        return this.contentLength;
    }
    
    public void setContentLength(final long contentLength) {
        this.contentLength = contentLength;
    }
    
    public String toString() {
        return this.name;
    }
    
    public String inspect() {
        return this.name + "[len = " + this.contentLength + "; mod = " + this.lastModified + "]";
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + (int)(this.contentLength ^ this.contentLength >>> 32);
        result = 31 * result + (int)(this.lastModified ^ this.lastModified >>> 32);
        result = 31 * result + ((this.name == null) ? 0 : this.name.hashCode());
        return result;
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Resource other = (Resource)obj;
        if (this.contentLength != other.contentLength) {
            return false;
        }
        if (this.lastModified != other.lastModified) {
            return false;
        }
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        }
        else if (!this.name.equals(other.name)) {
            return false;
        }
        return true;
    }
}
