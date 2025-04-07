// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon;

import org.apache.maven.wagon.resource.Resource;
import java.io.OutputStream;

public class OutputData
{
    private OutputStream outputStream;
    private Resource resource;
    
    public OutputStream getOutputStream() {
        return this.outputStream;
    }
    
    public void setOutputStream(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }
    
    public Resource getResource() {
        return this.resource;
    }
    
    public void setResource(final Resource resource) {
        this.resource = resource;
    }
}
