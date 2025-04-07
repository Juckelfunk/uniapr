// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util;

import java.io.File;

public interface Scanner
{
    void setIncludes(final String[] p0);
    
    void setExcludes(final String[] p0);
    
    void addDefaultExcludes();
    
    void scan();
    
    String[] getIncludedFiles();
    
    String[] getIncludedDirectories();
    
    File getBasedir();
}
