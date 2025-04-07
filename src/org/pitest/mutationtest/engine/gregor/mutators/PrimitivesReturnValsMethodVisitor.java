// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.mutators;

import java.util.HashMap;
import org.pitest.reloc.asm.Type;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import org.pitest.mutationtest.engine.gregor.ZeroOperandMutation;
import java.util.Map;
import org.pitest.mutationtest.engine.gregor.AbstractInsnMutator;

class PrimitivesReturnValsMethodVisitor extends AbstractInsnMutator
{
    private static final Map<Integer, ZeroOperandMutation> MUTATIONS;
    
    PrimitivesReturnValsMethodVisitor(final MethodMutatorFactory factory, final MethodInfo methodInfo, final MutationContext context, final MethodVisitor writer) {
        super(factory, methodInfo, context, writer);
    }
    
    private static ZeroOperandMutation lreturnMutation() {
        return new ZeroOperandMutation() {
            @Override
            public void apply(final int opcode, final MethodVisitor mv) {
                mv.visitInsn(88);
                mv.visitInsn(9);
                mv.visitInsn(173);
            }
            
            @Override
            public String decribe(final int opCode, final MethodInfo methodInfo) {
                return "replaced long return with 0 for " + methodInfo.getDescription();
            }
        };
    }
    
    private static ZeroOperandMutation freturnMutation() {
        return new ZeroOperandMutation() {
            @Override
            public void apply(final int opcode, final MethodVisitor mv) {
                mv.visitInsn(87);
                mv.visitInsn(11);
                mv.visitInsn(174);
            }
            
            @Override
            public String decribe(final int opCode, final MethodInfo methodInfo) {
                return "replaced float return with 0.0f for " + methodInfo.getDescription();
            }
        };
    }
    
    private static ZeroOperandMutation dreturnMutation() {
        return new ZeroOperandMutation() {
            @Override
            public void apply(final int opCode, final MethodVisitor mv) {
                mv.visitInsn(88);
                mv.visitInsn(14);
                mv.visitInsn(175);
            }
            
            @Override
            public String decribe(final int opCode, final MethodInfo methodInfo) {
                return "replaced double return with 0.0d for " + methodInfo.getDescription();
            }
        };
    }
    
    private static ZeroOperandMutation ireturnMutation() {
        return new ZeroOperandMutation() {
            @Override
            public void apply(final int opCode, final MethodVisitor mv) {
                mv.visitInsn(87);
                mv.visitInsn(3);
                mv.visitInsn(172);
            }
            
            @Override
            public String decribe(final int opCode, final MethodInfo methodInfo) {
                return this.makeMessage(methodInfo.getMethodDescriptor()) + " for " + methodInfo.getDescription();
            }
            
            private String makeMessage(final String methodDescriptor) {
                final int sort = Type.getReturnType(methodDescriptor).getSort();
                switch (sort) {
                    case 3: {
                        return "replaced byte return with 0";
                    }
                    case 5: {
                        return "replaced int return with 0";
                    }
                    case 2: {
                        return "replaced char return with 0";
                    }
                    case 4: {
                        return "replaced short return with 0";
                    }
                    default: {
                        throw new IllegalStateException(methodDescriptor + " does not return integer type");
                    }
                }
            }
        };
    }
    
    @Override
    protected Map<Integer, ZeroOperandMutation> getMutations() {
        return PrimitivesReturnValsMethodVisitor.MUTATIONS;
    }
    
    static {
        (MUTATIONS = new HashMap<Integer, ZeroOperandMutation>()).put(172, ireturnMutation());
        PrimitivesReturnValsMethodVisitor.MUTATIONS.put(175, dreturnMutation());
        PrimitivesReturnValsMethodVisitor.MUTATIONS.put(174, freturnMutation());
        PrimitivesReturnValsMethodVisitor.MUTATIONS.put(173, lreturnMutation());
    }
}
