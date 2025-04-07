// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.mutators;

import org.pitest.reloc.asm.Type;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;

public enum BooleanFalseReturnValsMutator implements MethodMutatorFactory
{
    BOOLEAN_FALSE_RETURN;
    
    @Override
    public MethodVisitor create(final MutationContext context, final MethodInfo methodInfo, final MethodVisitor methodVisitor) {
        final Type returnType = Type.getReturnType(methodInfo.getMethodDescriptor());
        if (returnType.getSort() == 1 || returnType.getClassName().equals("java.lang.Boolean")) {
            return new BooleanFalseMethodVisitor(this, methodInfo, context, methodVisitor);
        }
        return methodVisitor;
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
