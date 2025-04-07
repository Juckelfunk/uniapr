// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr;

import java.util.Map;
import java.io.File;

public interface PatchGenerationPlugin
{
    void generatePatches(final File p0, final File p1, final Map<String, String> p2) throws Exception;
    
    String name();
    
    String description();
}
