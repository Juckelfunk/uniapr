// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.mutators;

import org.pitest.functional.F2;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;

public enum NonVoidMethodCallMutator implements MethodMutatorFactory
{
    NON_VOID_METHOD_CALL_MUTATOR;
    
    @Override
    public MethodVisitor create(final MutationContext context, final MethodInfo methodInfo, final MethodVisitor methodVisitor) {
        return new MethodCallMethodVisitor(methodInfo, context, methodVisitor, this, this.nonVoidMethods());
    }
    
    @Override
    public String getGloballyUniqueId() {
        return this.getClass().getName();
    }
    
    @Override
    public String getName() {
        return this.name();
    }
    
    private F2<String, String, Boolean> nonVoidMethods() {
        return new F2<String, String, Boolean>() {
            @Override
            public Boolean apply(final String name, final String desc) {
                return !MethodInfo.isVoid(desc);
            }
        };
    }
}
