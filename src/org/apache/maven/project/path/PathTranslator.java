// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project.path;

import java.io.File;
import org.apache.maven.model.Model;

public interface PathTranslator
{
    public static final String ROLE = PathTranslator.class.getName();
    
    void alignToBaseDirectory(final Model p0, final File p1);
    
    String alignToBaseDirectory(final String p0, final File p1);
    
    void unalignFromBaseDirectory(final Model p0, final File p1);
    
    String unalignFromBaseDirectory(final String p0, final File p1);
}
