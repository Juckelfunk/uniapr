// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.mutators.experimental;

import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.reloc.asm.Label;
import org.pitest.reloc.asm.Type;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;

public class ReturnValuesMutator implements MethodMutatorFactory
{
    private static final ObjectMutationMethod OBJECT_MUTATION_METHOD;
    private static final ObjectReferenceReplacer SINGLETON_REPLACER;
    
    public static Object mutateObjectInstance(final Object object, final Class<?> clazz) {
        return ReturnValuesMutator.SINGLETON_REPLACER.replaceObjectInstance(object, clazz);
    }
    
    @Override
    public MethodVisitor create(final MutationContext context, final MethodInfo methodInfo, final MethodVisitor methodVisitor) {
        return new ReturnValuesMethodVisitor(context, methodInfo, methodVisitor);
    }
    
    @Override
    public String getGloballyUniqueId() {
        return this.getClass().getName();
    }
    
    @Override
    public String getName() {
        return "EXPERIMENTAL_RETURN_VALUES_MUTATOR";
    }
    
    static {
        OBJECT_MUTATION_METHOD = new ObjectMutationMethod();
        SINGLETON_REPLACER = new ObjectReferenceReplacer();
    }
    
    private static final class ObjectMutationMethod
    {
        private final String mutatorMethodName;
        private final String mutatorInternalName;
        private final String mutationMethodDescriptor;
        
        ObjectMutationMethod() {
            final Type mutatorType = Type.getType(ReturnValuesMutator.class);
            this.mutatorInternalName = mutatorType.getInternalName();
            this.mutatorMethodName = "mutateObjectInstance";
            final Type objectType = Type.getType(Object.class);
            final Type classType = Type.getType(Class.class);
            this.mutationMethodDescriptor = Type.getMethodDescriptor(objectType, objectType, classType);
        }
        
        public String getClassName() {
            return this.mutatorInternalName;
        }
        
        public String getMethodDescriptor() {
            return this.mutationMethodDescriptor;
        }
        
        public String getMethodName() {
            return this.mutatorMethodName;
        }
    }
    
    private static final class ObjectReferenceReplacer
    {
        private Object replaceObjectInstance(final Object object, final Class<?> declaredReturnType) {
            if (Boolean.class == declaredReturnType) {
                if (Boolean.TRUE.equals(object)) {
                    return Boolean.FALSE;
                }
                return Boolean.TRUE;
            }
            else if (Integer.class == declaredReturnType) {
                final Integer intValue = (Integer)object;
                if (intValue == null) {
                    return 1;
                }
                if (intValue == 1) {
                    return 0;
                }
                return intValue + 1;
            }
            else if (Long.class == declaredReturnType) {
                final Long longValue = (Long)object;
                if (longValue == null) {
                    return 1L;
                }
                return longValue + 1L;
            }
            else if (Object.class == declaredReturnType) {
                if (object != null) {
                    return null;
                }
                return new Object();
            }
            else {
                if (object == null) {
                    throw new RuntimeException("Mutated return of null object to throwing a runtime exception");
                }
                return null;
            }
        }
    }
    
    private final class ReturnValuesMethodVisitor extends MethodVisitor
    {
        private static final String DESCRIPTION_MESSAGE_PATTERN = "replaced return of %s value with %s";
        private final MutationContext context;
        private final MethodInfo methodInfo;
        
        private ReturnValuesMethodVisitor(final MutationContext context, final MethodInfo methodInfo, final MethodVisitor delegateVisitor) {
            super(393216, delegateVisitor);
            this.context = context;
            this.methodInfo = methodInfo;
        }
        
        private void mutateObjectReferenceReturn() {
            if (this.shouldMutate("object reference", "[see docs for details]")) {
                final Type returnType = this.methodInfo.getReturnType();
                super.visitLdcInsn(returnType);
                super.visitMethodInsn(184, ReturnValuesMutator.OBJECT_MUTATION_METHOD.getClassName(), ReturnValuesMutator.OBJECT_MUTATION_METHOD.getMethodName(), ReturnValuesMutator.OBJECT_MUTATION_METHOD.getMethodDescriptor(), false);
                super.visitTypeInsn(192, returnType.getInternalName());
            }
            super.visitInsn(176);
        }
        
        private void mutatePrimitiveDoubleReturn() {
            if (this.shouldMutate("primitive double", "(x != NaN)? -(x + 1) : -1 ")) {
                final Label label = new Label();
                super.visitInsn(92);
                super.visitInsn(92);
                super.visitInsn(152);
                super.visitJumpInsn(153, label);
                super.visitInsn(88);
                super.visitInsn(14);
                super.visitLabel(label);
                super.visitInsn(15);
                super.visitInsn(99);
                super.visitInsn(119);
                super.visitInsn(175);
            }
        }
        
        private void mutatePrimitiveFloatReturn() {
            if (this.shouldMutate("primitive float", "(x != NaN)? -(x + 1) : -1 ")) {
                final Label label = new Label();
                super.visitInsn(89);
                super.visitInsn(89);
                super.visitInsn(150);
                super.visitJumpInsn(153, label);
                super.visitInsn(87);
                super.visitInsn(11);
                super.visitLabel(label);
                super.visitInsn(12);
                super.visitInsn(98);
                super.visitInsn(118);
                super.visitInsn(174);
            }
        }
        
        private void mutatePrimitiveIntegerReturn() {
            if (this.shouldMutate("primitive boolean/byte/short/integer", "(x == 1) ? 0 : x + 1")) {
                final Label label = new Label();
                super.visitInsn(89);
                super.visitInsn(4);
                super.visitJumpInsn(159, label);
                super.visitInsn(4);
                super.visitInsn(96);
                super.visitInsn(172);
                super.visitLabel(label);
                super.visitInsn(3);
                super.visitInsn(172);
            }
        }
        
        private void mutatePrimitiveLongReturn() {
            if (this.shouldMutate("primitive long", "x + 1")) {
                super.visitInsn(10);
                super.visitInsn(97);
                super.visitInsn(173);
            }
        }
        
        private boolean shouldMutate(final String type, final String replacement) {
            final String description = String.format("replaced return of %s value with %s", type, replacement);
            final MutationIdentifier mutationId = this.context.registerMutation(ReturnValuesMutator.this, description);
            return this.context.shouldMutate(mutationId);
        }
        
        @Override
        public void visitInsn(final int opcode) {
            switch (opcode) {
                case 172: {
                    this.mutatePrimitiveIntegerReturn();
                    break;
                }
                case 173: {
                    this.mutatePrimitiveLongReturn();
                    break;
                }
                case 174: {
                    this.mutatePrimitiveFloatReturn();
                    break;
                }
                case 175: {
                    this.mutatePrimitiveDoubleReturn();
                    break;
                }
                case 176: {
                    this.mutateObjectReferenceReturn();
                    break;
                }
                default: {
                    super.visitInsn(opcode);
                    break;
                }
            }
        }
    }
}
