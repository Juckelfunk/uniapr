// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.mutators;

import java.util.HashMap;
import org.pitest.reloc.asm.Label;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import org.pitest.mutationtest.engine.gregor.ZeroOperandMutation;
import java.util.Map;
import org.pitest.mutationtest.engine.gregor.AbstractInsnMutator;

class ReturnValsMethodVisitor extends AbstractInsnMutator
{
    private static final Map<Integer, ZeroOperandMutation> MUTATIONS;
    
    ReturnValsMethodVisitor(final MethodMutatorFactory factory, final MethodInfo methodInfo, final MutationContext context, final MethodVisitor writer) {
        super(factory, methodInfo, context, writer);
    }
    
    private static ZeroOperandMutation areturnMutation() {
        return new ZeroOperandMutation() {
            @Override
            public void apply(final int opCode, final MethodVisitor mv) {
                final Label l1 = new Label();
                mv.visitJumpInsn(199, l1);
                mv.visitTypeInsn(187, "java/lang/RuntimeException");
                mv.visitInsn(89);
                mv.visitMethodInsn(183, "java/lang/RuntimeException", "<init>", "()V", false);
                mv.visitInsn(191);
                mv.visitLabel(l1);
                mv.visitInsn(1);
                mv.visitInsn(176);
            }
            
            @Override
            public String decribe(final int opCode, final MethodInfo methodInfo) {
                return "mutated return of Object value for " + methodInfo.getDescription() + " to ( if (x != null) null else throw new RuntimeException )";
            }
        };
    }
    
    private static ZeroOperandMutation lreturnMutation() {
        return new ZeroOperandMutation() {
            @Override
            public void apply(final int opcode, final MethodVisitor mv) {
                mv.visitInsn(10);
                mv.visitInsn(97);
                mv.visitInsn(opcode);
            }
            
            @Override
            public String decribe(final int opCode, final MethodInfo methodInfo) {
                return "replaced return of long value with value + 1 for " + methodInfo.getDescription();
            }
        };
    }
    
    private static ZeroOperandMutation freturnMutation() {
        return new ZeroOperandMutation() {
            @Override
            public void apply(final int opcode, final MethodVisitor mv) {
                mv.visitInsn(89);
                mv.visitInsn(89);
                mv.visitInsn(150);
                final Label l1 = new Label();
                mv.visitJumpInsn(153, l1);
                mv.visitInsn(87);
                mv.visitInsn(11);
                mv.visitLabel(l1);
                mv.visitInsn(12);
                mv.visitInsn(98);
                mv.visitInsn(118);
                mv.visitInsn(174);
            }
            
            @Override
            public String decribe(final int opCode, final MethodInfo methodInfo) {
                return "replaced return of float value with -(x + 1) for " + methodInfo.getDescription();
            }
        };
    }
    
    private static ZeroOperandMutation dreturnMutation() {
        return new ZeroOperandMutation() {
            @Override
            public void apply(final int opCode, final MethodVisitor mv) {
                mv.visitInsn(92);
                mv.visitInsn(92);
                mv.visitInsn(152);
                final Label l1 = new Label();
                mv.visitJumpInsn(153, l1);
                mv.visitInsn(88);
                mv.visitInsn(14);
                mv.visitLabel(l1);
                mv.visitInsn(15);
                mv.visitInsn(99);
                mv.visitInsn(119);
                mv.visitInsn(175);
            }
            
            @Override
            public String decribe(final int opCode, final MethodInfo methodInfo) {
                return "replaced return of double value with -(x + 1) for " + methodInfo.getDescription();
            }
        };
    }
    
    private static ZeroOperandMutation ireturnMutation() {
        return new ZeroOperandMutation() {
            @Override
            public void apply(final int opCode, final MethodVisitor mv) {
                final Label l1 = new Label();
                mv.visitJumpInsn(153, l1);
                mv.visitInsn(3);
                mv.visitInsn(172);
                mv.visitLabel(l1);
                mv.visitInsn(4);
                mv.visitInsn(172);
            }
            
            @Override
            public String decribe(final int opCode, final MethodInfo methodInfo) {
                return "replaced return of integer sized value with (x == 0 ? 1 : 0)";
            }
        };
    }
    
    @Override
    protected Map<Integer, ZeroOperandMutation> getMutations() {
        return ReturnValsMethodVisitor.MUTATIONS;
    }
    
    static {
        (MUTATIONS = new HashMap<Integer, ZeroOperandMutation>()).put(172, ireturnMutation());
        ReturnValsMethodVisitor.MUTATIONS.put(175, dreturnMutation());
        ReturnValsMethodVisitor.MUTATIONS.put(174, freturnMutation());
        ReturnValsMethodVisitor.MUTATIONS.put(173, lreturnMutation());
        ReturnValsMethodVisitor.MUTATIONS.put(176, areturnMutation());
    }
}
