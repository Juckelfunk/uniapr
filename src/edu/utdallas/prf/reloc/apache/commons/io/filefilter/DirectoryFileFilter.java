// 
// Decompiled by Procyon v0.5.36
// 

package edu.utdallas.prf.reloc.apache.commons.io.filefilter;

import java.io.File;
import java.io.Serializable;

public class DirectoryFileFilter extends AbstractFileFilter implements Serializable
{
    private static final long serialVersionUID = -5148237843784525732L;
    public static final IOFileFilter DIRECTORY;
    public static final IOFileFilter INSTANCE;
    
    protected DirectoryFileFilter() {
    }
    
    @Override
    public boolean accept(final File file) {
        return file.isDirectory();
    }
    
    static {
        DIRECTORY = new DirectoryFileFilter();
        INSTANCE = DirectoryFileFilter.DIRECTORY;
    }
}
