// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.mutators;

import java.util.HashMap;
import org.pitest.classinfo.ClassName;
import org.pitest.reloc.asm.Type;
import java.util.Collections;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import org.pitest.mutationtest.engine.gregor.ZeroOperandMutation;
import java.util.Map;
import org.pitest.mutationtest.engine.gregor.AbstractInsnMutator;

class AReturnMethodVisitor extends AbstractInsnMutator
{
    static final Map<String, ZeroOperandMutation> NON_NULL_MUTATIONS;
    
    AReturnMethodVisitor(final MethodMutatorFactory factory, final MethodInfo methodInfo, final MutationContext context, final MethodVisitor writer) {
        super(factory, methodInfo, context, writer);
    }
    
    @Override
    protected boolean canMutate(final int opcode) {
        return super.canMutate(opcode) && this.canMutateToNonNull();
    }
    
    @Override
    protected Map<Integer, ZeroOperandMutation> getMutations() {
        return Collections.singletonMap(176, this.areturnMutation());
    }
    
    private ZeroOperandMutation areturnMutation() {
        return AReturnMethodVisitor.NON_NULL_MUTATIONS.get(this.currentReturnType());
    }
    
    private boolean canMutateToNonNull() {
        return AReturnMethodVisitor.NON_NULL_MUTATIONS.containsKey(this.currentReturnType());
    }
    
    private String currentReturnType() {
        final Type t = Type.getReturnType(this.methodInfo().getMethodDescriptor());
        return t.getClassName();
    }
    
    private static ZeroOperandMutation returnIntegerZero(final Class<?> owner, final String sig, final String msg) {
        return new ZeroOperandMutation() {
            @Override
            public void apply(final int opCode, final MethodVisitor mv) {
                mv.visitInsn(87);
                mv.visitInsn(3);
                mv.visitMethodInsn(184, ClassName.fromClass(owner).asInternalName(), "valueOf", sig, false);
                mv.visitInsn(176);
            }
            
            @Override
            public String decribe(final int opCode, final MethodInfo methodInfo) {
                return msg + " for " + methodInfo.getDescription();
            }
        };
    }
    
    private static ZeroOperandMutation returnLongZero() {
        return new ZeroOperandMutation() {
            @Override
            public void apply(final int opCode, final MethodVisitor mv) {
                mv.visitInsn(87);
                mv.visitInsn(9);
                mv.visitMethodInsn(184, ClassName.fromClass(Long.class).asInternalName(), "valueOf", "(J)Ljava/lang/Long;", false);
                mv.visitInsn(176);
            }
            
            @Override
            public String decribe(final int opCode, final MethodInfo methodInfo) {
                return "replaced Long return value with 0L for " + methodInfo.getDescription();
            }
        };
    }
    
    private static ZeroOperandMutation returnDoubleZero() {
        return new ZeroOperandMutation() {
            @Override
            public void apply(final int opCode, final MethodVisitor mv) {
                mv.visitInsn(87);
                mv.visitInsn(14);
                mv.visitMethodInsn(184, ClassName.fromClass(Double.class).asInternalName(), "valueOf", "(D)Ljava/lang/Double;", false);
                mv.visitInsn(176);
            }
            
            @Override
            public String decribe(final int opCode, final MethodInfo methodInfo) {
                return "replaced Double return value with 0 for " + methodInfo.getDescription();
            }
        };
    }
    
    private static ZeroOperandMutation returnFloatZero() {
        return new ZeroOperandMutation() {
            @Override
            public void apply(final int opCode, final MethodVisitor mv) {
                mv.visitInsn(87);
                mv.visitInsn(11);
                mv.visitMethodInsn(184, ClassName.fromClass(Float.class).asInternalName(), "valueOf", "(F)Ljava/lang/Float;", false);
                mv.visitInsn(176);
            }
            
            @Override
            public String decribe(final int opCode, final MethodInfo methodInfo) {
                return "replaced Float return value with 0 for " + methodInfo.getDescription();
            }
        };
    }
    
    private static ZeroOperandMutation returnEmptyString() {
        return new ZeroOperandMutation() {
            @Override
            public void apply(final int opCode, final MethodVisitor mv) {
                mv.visitInsn(87);
                mv.visitLdcInsn("");
                mv.visitInsn(176);
            }
            
            @Override
            public String decribe(final int opCode, final MethodInfo methodInfo) {
                return "replaced return value with \"\" for " + methodInfo.getDescription();
            }
        };
    }
    
    private static ZeroOperandMutation returnEmptyList() {
        return new ZeroOperandMutation() {
            @Override
            public void apply(final int opCode, final MethodVisitor mv) {
                mv.visitInsn(87);
                mv.visitMethodInsn(184, "java/util/Collections", "emptyList", "()Ljava/util/List;", false);
                mv.visitInsn(176);
            }
            
            @Override
            public String decribe(final int opCode, final MethodInfo methodInfo) {
                return "replaced return value with Collections.emptyList for " + methodInfo.getDescription();
            }
        };
    }
    
    private static ZeroOperandMutation returnEmptySet() {
        return new ZeroOperandMutation() {
            @Override
            public void apply(final int opCode, final MethodVisitor mv) {
                mv.visitInsn(87);
                mv.visitMethodInsn(184, "java/util/Collections", "emptySet", "()Ljava/util/Set;", false);
                mv.visitInsn(176);
            }
            
            @Override
            public String decribe(final int opCode, final MethodInfo methodInfo) {
                return "replaced return value with Collections.emptyList for " + methodInfo.getDescription();
            }
        };
    }
    
    private static ZeroOperandMutation returnEmptyOptional() {
        return new ZeroOperandMutation() {
            @Override
            public void apply(final int opCode, final MethodVisitor mv) {
                mv.visitInsn(87);
                mv.visitMethodInsn(184, "java/util/Optional", "empty", "()Ljava/util/Optional;", false);
                mv.visitInsn(176);
            }
            
            @Override
            public String decribe(final int opCode, final MethodInfo methodInfo) {
                return "replaced return value with Optional.empty for " + methodInfo.getDescription();
            }
        };
    }
    
    static {
        (NON_NULL_MUTATIONS = new HashMap<String, ZeroOperandMutation>()).put("java.lang.Integer", returnIntegerZero(Integer.class, "(I)Ljava/lang/Integer;", "replaced Integer return value with 0"));
        AReturnMethodVisitor.NON_NULL_MUTATIONS.put("java.lang.Short", returnIntegerZero(Short.class, "(S)Ljava/lang/Short;", "replaced Short return value with 0"));
        AReturnMethodVisitor.NON_NULL_MUTATIONS.put("java.lang.Character", returnIntegerZero(Character.class, "(C)Ljava/lang/Character;", "replaced Character return value with 0"));
        AReturnMethodVisitor.NON_NULL_MUTATIONS.put("java.lang.Long", returnLongZero());
        AReturnMethodVisitor.NON_NULL_MUTATIONS.put("java.lang.Float", returnFloatZero());
        AReturnMethodVisitor.NON_NULL_MUTATIONS.put("java.lang.Double", returnDoubleZero());
        AReturnMethodVisitor.NON_NULL_MUTATIONS.put("java.lang.String", returnEmptyString());
        AReturnMethodVisitor.NON_NULL_MUTATIONS.put("java.util.Optional", returnEmptyOptional());
        AReturnMethodVisitor.NON_NULL_MUTATIONS.put("java.util.List", returnEmptyList());
        AReturnMethodVisitor.NON_NULL_MUTATIONS.put("java.util.Set", returnEmptySet());
        AReturnMethodVisitor.NON_NULL_MUTATIONS.put("java.util.Collection", returnEmptyList());
    }
}
