// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.mutators;

import java.util.HashMap;
import org.pitest.classinfo.ClassName;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import org.pitest.mutationtest.engine.gregor.ZeroOperandMutation;
import java.util.Map;
import org.pitest.mutationtest.engine.gregor.AbstractInsnMutator;

class BooleanTrueMethodVisitor extends AbstractInsnMutator
{
    private static final String BOOLEAN;
    private static final Map<Integer, ZeroOperandMutation> MUTATIONS;
    
    BooleanTrueMethodVisitor(final MethodMutatorFactory factory, final MethodInfo methodInfo, final MutationContext context, final MethodVisitor writer) {
        super(factory, methodInfo, context, writer);
    }
    
    private static ZeroOperandMutation ireturnMutation() {
        return new ZeroOperandMutation() {
            @Override
            public void apply(final int opCode, final MethodVisitor mv) {
                mv.visitInsn(87);
                mv.visitInsn(4);
                mv.visitInsn(172);
            }
            
            @Override
            public String decribe(final int opCode, final MethodInfo methodInfo) {
                return "replaced boolean return with true for " + methodInfo.getDescription();
            }
        };
    }
    
    private static ZeroOperandMutation areturnMutation() {
        return new ZeroOperandMutation() {
            @Override
            public void apply(final int opCode, final MethodVisitor mv) {
                mv.visitInsn(87);
                mv.visitInsn(4);
                mv.visitMethodInsn(184, BooleanTrueMethodVisitor.BOOLEAN, "valueOf", "(Z)Ljava/lang/Boolean;", false);
                mv.visitInsn(176);
            }
            
            @Override
            public String decribe(final int opCode, final MethodInfo methodInfo) {
                return "replaced Boolean return with True for " + methodInfo.getDescription();
            }
        };
    }
    
    @Override
    protected Map<Integer, ZeroOperandMutation> getMutations() {
        return BooleanTrueMethodVisitor.MUTATIONS;
    }
    
    static {
        BOOLEAN = ClassName.fromClass(Boolean.class).asInternalName();
        (MUTATIONS = new HashMap<Integer, ZeroOperandMutation>()).put(172, ireturnMutation());
        BooleanTrueMethodVisitor.MUTATIONS.put(176, areturnMutation());
    }
}
