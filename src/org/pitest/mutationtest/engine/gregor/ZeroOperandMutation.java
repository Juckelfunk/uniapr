// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor;

import org.pitest.reloc.asm.MethodVisitor;

public interface ZeroOperandMutation
{
    void apply(final int p0, final MethodVisitor p1);
    
    String decribe(final int p0, final MethodInfo p1);
}
