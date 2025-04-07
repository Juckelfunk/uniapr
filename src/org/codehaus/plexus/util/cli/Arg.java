// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.cli;

import java.io.File;

public interface Arg
{
    void setValue(final String p0);
    
    void setLine(final String p0);
    
    void setFile(final File p0);
    
    String[] getParts();
}
