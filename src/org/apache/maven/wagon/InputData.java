// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon;

import org.apache.maven.wagon.resource.Resource;
import java.io.InputStream;

public class InputData
{
    private InputStream inputStream;
    private Resource resource;
    
    public InputStream getInputStream() {
        return this.inputStream;
    }
    
    public void setInputStream(final InputStream inputStream) {
        this.inputStream = inputStream;
    }
    
    public Resource getResource() {
        return this.resource;
    }
    
    public void setResource(final Resource resource) {
        this.resource = resource;
    }
}
