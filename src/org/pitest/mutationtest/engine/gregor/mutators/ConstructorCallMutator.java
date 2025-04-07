// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.mutators;

import org.pitest.functional.F2;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;

public enum ConstructorCallMutator implements MethodMutatorFactory
{
    CONSTRUCTOR_CALL_MUTATOR;
    
    @Override
    public MethodVisitor create(final MutationContext context, final MethodInfo methodInfo, final MethodVisitor methodVisitor) {
        return new MethodCallMethodVisitor(methodInfo, context, methodVisitor, this, constructors());
    }
    
    @Override
    public String getGloballyUniqueId() {
        return this.getClass().getName();
    }
    
    private static F2<String, String, Boolean> constructors() {
        return new F2<String, String, Boolean>() {
            @Override
            public Boolean apply(final String name, final String desc) {
                return MethodInfo.isConstructor(name);
            }
        };
    }
    
    @Override
    public String getName() {
        return this.name();
    }
}
