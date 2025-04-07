// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.mutators;

import java.util.Arrays;
import java.util.List;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;

public class NullMutateEverything implements MethodMutatorFactory
{
    @Override
    public MethodVisitor create(final MutationContext context, final MethodInfo methodInfo, final MethodVisitor methodVisitor) {
        return new MutateEveryThing(this, context, methodVisitor);
    }
    
    @Override
    public String getGloballyUniqueId() {
        return "mutateallthethings";
    }
    
    @Override
    public String getName() {
        return "mutateeverything";
    }
    
    public static List<MethodMutatorFactory> asList() {
        return Arrays.asList(new NullMutateEverything());
    }
}
