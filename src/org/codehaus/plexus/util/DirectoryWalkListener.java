// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util;

import java.io.File;

public interface DirectoryWalkListener
{
    void directoryWalkStarting(final File p0);
    
    void directoryWalkStep(final int p0, final File p1);
    
    void directoryWalkFinished();
    
    void debug(final String p0);
}
