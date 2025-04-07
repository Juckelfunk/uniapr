// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.mutators;

import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;

public enum MathMutator implements MethodMutatorFactory
{
    MATH_MUTATOR;
    
    @Override
    public MethodVisitor create(final MutationContext context, final MethodInfo methodInfo, final MethodVisitor methodVisitor) {
        return new MathMethodVisitor(this, methodInfo, context, methodVisitor);
    }
    
    @Override
    public String getGloballyUniqueId() {
        return this.getClass().getName();
    }
    
    @Override
    public String getName() {
        return this.name();
    }
}
