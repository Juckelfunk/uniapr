// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.io;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.File;

public class FileInputStreamFacade implements InputStreamFacade
{
    private final File file;
    
    public FileInputStreamFacade(final File file) {
        this.file = file;
    }
    
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(this.file);
    }
}
