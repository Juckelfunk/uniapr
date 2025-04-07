// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.mutators.experimental;

import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.reloc.asm.Type;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;

public class MemberVariableMutator implements MethodMutatorFactory
{
    @Override
    public MethodVisitor create(final MutationContext context, final MethodInfo methodInfo, final MethodVisitor methodVisitor) {
        return new MemberVariableVisitor(context, methodVisitor);
    }
    
    @Override
    public String getGloballyUniqueId() {
        return this.getClass().getName();
    }
    
    @Override
    public String toString() {
        return "EXPERIMENTAL_MEMBER_VARIABLE_MUTATOR";
    }
    
    @Override
    public String getName() {
        return this.toString();
    }
    
    private final class MemberVariableVisitor extends MethodVisitor
    {
        private final MutationContext context;
        
        MemberVariableVisitor(final MutationContext context, final MethodVisitor delegateVisitor) {
            super(393216, delegateVisitor);
            this.context = context;
        }
        
        @Override
        public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
            if (181 == opcode && this.shouldMutate(name)) {
                if (Type.getType(desc).getSize() == 2) {
                    super.visitInsn(88);
                    super.visitInsn(87);
                }
                else {
                    super.visitInsn(87);
                    super.visitInsn(87);
                }
            }
            else {
                super.visitFieldInsn(opcode, owner, name, desc);
            }
        }
        
        @Override
        public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }
        
        private boolean shouldMutate(final String fieldName) {
            final MutationIdentifier mutationId = this.context.registerMutation(MemberVariableMutator.this, "Removed assignment to member variable " + fieldName);
            return this.context.shouldMutate(mutationId);
        }
    }
}
