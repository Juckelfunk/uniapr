// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.mutators;

import java.util.Collections;
import org.pitest.mutationtest.engine.gregor.ZeroOperandMutation;
import java.util.Map;
import org.pitest.reloc.asm.AnnotationVisitor;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import org.pitest.mutationtest.engine.gregor.AbstractInsnMutator;

class NullReturnMethodVisitor extends AbstractInsnMutator
{
    private boolean hasNotNullAnnotation;
    
    NullReturnMethodVisitor(final MethodMutatorFactory factory, final MethodInfo methodInfo, final MutationContext context, final MethodVisitor writer) {
        super(factory, methodInfo, context, writer);
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        this.hasNotNullAnnotation |= desc.endsWith("NotNull;");
        return super.visitAnnotation(desc, visible);
    }
    
    @Override
    protected boolean canMutate(final int opcode) {
        return super.canMutate(opcode) && !this.hasNotNullAnnotation;
    }
    
    @Override
    protected Map<Integer, ZeroOperandMutation> getMutations() {
        return Collections.singletonMap(176, nullReturn());
    }
    
    private static ZeroOperandMutation nullReturn() {
        return new ZeroOperandMutation() {
            @Override
            public void apply(final int opCode, final MethodVisitor mv) {
                mv.visitInsn(87);
                mv.visitInsn(1);
                mv.visitInsn(176);
            }
            
            @Override
            public String decribe(final int opCode, final MethodInfo methodInfo) {
                return "replaced return value with null for " + methodInfo.getDescription();
            }
        };
    }
}
