// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.mutators;

import org.pitest.reloc.asm.Type;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;

public enum EmptyObjectReturnValsMutator implements MethodMutatorFactory
{
    EMPTY_RETURN_VALUES;
    
    @Override
    public MethodVisitor create(final MutationContext context, final MethodInfo methodInfo, final MethodVisitor methodVisitor) {
        if (!this.returnsBoolean(methodInfo)) {
            return new AReturnMethodVisitor(this, methodInfo, context, methodVisitor);
        }
        return methodVisitor;
    }
    
    private boolean returnsBoolean(final MethodInfo methodInfo) {
        final Type type = Type.getReturnType(methodInfo.getMethodDescriptor());
        return type.getClassName().equals("java.lang.Boolean");
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
