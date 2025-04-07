// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.process;

import org.pitest.functional.Option;

public interface JavaAgent
{
    Option<String> getJarLocation();
    
    void close();
}
