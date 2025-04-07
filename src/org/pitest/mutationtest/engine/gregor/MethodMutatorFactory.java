// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor;

import org.pitest.reloc.asm.MethodVisitor;

public interface MethodMutatorFactory
{
    MethodVisitor create(final MutationContext p0, final MethodInfo p1, final MethodVisitor p2);
    
    String getGloballyUniqueId();
    
    String getName();
}
