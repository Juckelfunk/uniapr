// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.incremental;

import java.io.PrintWriter;

public interface WriterFactory
{
    PrintWriter create();
    
    void close();
}
