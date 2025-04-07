// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.mutators;

import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.util.PitError;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;

public class InlineConstantMutator implements MethodMutatorFactory
{
    @Override
    public MethodVisitor create(final MutationContext context, final MethodInfo methodInfo, final MethodVisitor methodVisitor) {
        return new InlineConstantVisitor(context, methodVisitor);
    }
    
    @Override
    public String getGloballyUniqueId() {
        return this.getClass().getName();
    }
    
    @Override
    public String toString() {
        return "INLINE_CONSTANT_MUTATOR";
    }
    
    @Override
    public String getName() {
        return this.toString();
    }
    
    private final class InlineConstantVisitor extends MethodVisitor
    {
        private final MutationContext context;
        
        InlineConstantVisitor(final MutationContext context, final MethodVisitor delegateVisitor) {
            super(393216, delegateVisitor);
            this.context = context;
        }
        
        private void mutate(final Double constant) {
            final Double replacement = (constant == 1.0) ? 2.0 : 1.0;
            if (this.shouldMutate(constant, replacement)) {
                this.translateToByteCode(replacement);
            }
            else {
                this.translateToByteCode(constant);
            }
        }
        
        private void mutate(final Float constant) {
            final Float replacement = (constant == 1.0f) ? 2.0f : 1.0f;
            if (this.shouldMutate(constant, replacement)) {
                this.translateToByteCode(replacement);
            }
            else {
                this.translateToByteCode(constant);
            }
        }
        
        private void mutate(final Integer constant) {
            Integer replacement = null;
            switch (constant) {
                case 1: {
                    replacement = 0;
                    break;
                }
                case 127: {
                    replacement = -128;
                    break;
                }
                case 32767: {
                    replacement = -32768;
                    break;
                }
                default: {
                    replacement = constant + 1;
                    break;
                }
            }
            if (this.shouldMutate(constant, replacement)) {
                this.translateToByteCode(replacement);
            }
            else {
                this.translateToByteCode(constant);
            }
        }
        
        private void mutate(final Long constant) {
            final Long replacement = constant + 1L;
            if (this.shouldMutate(constant, replacement)) {
                this.translateToByteCode(replacement);
            }
            else {
                this.translateToByteCode(constant);
            }
        }
        
        private void mutate(final Number constant) {
            if (constant instanceof Integer) {
                this.mutate((Integer)constant);
            }
            else if (constant instanceof Long) {
                this.mutate((Long)constant);
            }
            else if (constant instanceof Float) {
                this.mutate((Float)constant);
            }
            else {
                if (!(constant instanceof Double)) {
                    throw new PitError("Unsupported subtype of Number found:" + constant.getClass());
                }
                this.mutate((Double)constant);
            }
        }
        
        private <T extends Number> boolean shouldMutate(final T constant, final T replacement) {
            final MutationIdentifier mutationId = this.context.registerMutation(InlineConstantMutator.this, "Substituted " + constant + " with " + replacement);
            return this.context.shouldMutate(mutationId);
        }
        
        private void translateToByteCode(final Double constant) {
            if (constant == 0.0) {
                super.visitInsn(14);
            }
            else if (constant == 1.0) {
                super.visitInsn(15);
            }
            else {
                super.visitLdcInsn(constant);
            }
        }
        
        private void translateToByteCode(final Float constant) {
            if (constant == 0.0f) {
                super.visitInsn(11);
            }
            else if (constant == 1.0f) {
                super.visitInsn(12);
            }
            else if (constant == 2.0f) {
                super.visitInsn(13);
            }
            else {
                super.visitLdcInsn(constant);
            }
        }
        
        private void translateToByteCode(final Integer constant) {
            switch (constant) {
                case -1: {
                    super.visitInsn(2);
                    break;
                }
                case 0: {
                    super.visitInsn(3);
                    break;
                }
                case 1: {
                    super.visitInsn(4);
                    break;
                }
                case 2: {
                    super.visitInsn(5);
                    break;
                }
                case 3: {
                    super.visitInsn(6);
                    break;
                }
                case 4: {
                    super.visitInsn(7);
                    break;
                }
                case 5: {
                    super.visitInsn(8);
                    break;
                }
                default: {
                    super.visitLdcInsn(constant);
                    break;
                }
            }
        }
        
        private void translateToByteCode(final Long constant) {
            if (constant == 0L) {
                super.visitInsn(9);
            }
            else if (constant == 1L) {
                super.visitInsn(10);
            }
            else {
                super.visitLdcInsn(constant);
            }
        }
        
        private Number translateToNumber(final int opcode) {
            switch (opcode) {
                case 2: {
                    return -1;
                }
                case 3: {
                    return 0;
                }
                case 4: {
                    return 1;
                }
                case 5: {
                    return 2;
                }
                case 6: {
                    return 3;
                }
                case 7: {
                    return 4;
                }
                case 8: {
                    return 5;
                }
                case 9: {
                    return 0L;
                }
                case 10: {
                    return 1L;
                }
                case 11: {
                    return 0.0f;
                }
                case 12: {
                    return 1.0f;
                }
                case 13: {
                    return 2.0f;
                }
                case 14: {
                    return 0.0;
                }
                case 15: {
                    return 1.0;
                }
                default: {
                    return null;
                }
            }
        }
        
        @Override
        public void visitInsn(final int opcode) {
            final Number inlineConstant = this.translateToNumber(opcode);
            if (inlineConstant == null) {
                super.visitInsn(opcode);
                return;
            }
            this.mutate(inlineConstant);
        }
        
        @Override
        public void visitIntInsn(final int opcode, final int operand) {
            if (opcode == 16 || opcode == 17) {
                this.mutate(operand);
            }
            else {
                super.visitIntInsn(opcode, operand);
            }
        }
        
        @Override
        public void visitLdcInsn(final Object constant) {
            if (constant instanceof Number) {
                this.mutate((Number)constant);
            }
            else {
                super.visitLdcInsn(constant);
            }
        }
    }
}
