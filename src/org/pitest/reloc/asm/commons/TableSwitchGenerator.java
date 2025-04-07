// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.reloc.asm.commons;

import org.pitest.reloc.asm.Label;

public interface TableSwitchGenerator
{
    void generateCase(final int p0, final Label p1);
    
    void generateDefault();
}
