// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.io;

import java.io.IOException;
import java.io.InputStream;

public class RawInputStreamFacade implements InputStreamFacade
{
    final InputStream stream;
    
    public RawInputStreamFacade(final InputStream stream) {
        this.stream = stream;
    }
    
    public InputStream getInputStream() throws IOException {
        return this.stream;
    }
}
