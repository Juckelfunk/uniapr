// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.mutators.experimental;

import org.pitest.reloc.asm.Type;
import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;

public enum NakedReceiverMutator implements MethodMutatorFactory
{
    NAKED_RECEIVER;
    
    @Override
    public MethodVisitor create(final MutationContext context, final MethodInfo methodInfo, final MethodVisitor methodVisitor) {
        return new ReplaceMethodCallWithObjectVisitor(context, methodVisitor, this);
    }
    
    @Override
    public String getGloballyUniqueId() {
        return this.getClass().getName();
    }
    
    @Override
    public String getName() {
        return this.name();
    }
    
    static class ReplaceMethodCallWithObjectVisitor extends MethodVisitor
    {
        private final MethodMutatorFactory factory;
        private final MutationContext context;
        
        ReplaceMethodCallWithObjectVisitor(final MutationContext context, final MethodVisitor writer, final MethodMutatorFactory factory) {
            super(393216, writer);
            this.factory = factory;
            this.context = context;
        }
        
        @Override
        public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
            if (this.isNonStaticCall(opcode) && this.hasReturnTypeMatchingReceiverType(desc, owner)) {
                final MutationIdentifier newId = this.context.registerMutation(this.factory, "replaced call to " + owner + "::" + name + " with receiver");
                if (this.context.shouldMutate(newId)) {
                    this.popMethodArgumentsFromStack(desc);
                    return;
                }
                this.mv.visitMethodInsn(opcode, owner, name, desc, itf);
            }
            else {
                this.mv.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }
        
        private boolean hasReturnTypeMatchingReceiverType(final String desc, final String owner) {
            return Type.getObjectType(owner).equals(Type.getReturnType(desc));
        }
        
        private void popMethodArgumentsFromStack(final String desc) {
            final Type[] argumentTypes2;
            final Type[] argumentTypes = argumentTypes2 = Type.getArgumentTypes(desc);
            for (final Type argType : argumentTypes2) {
                this.popArgument(argType);
            }
        }
        
        private void popArgument(final Type argumentType) {
            if (argumentType.getSize() != 1) {
                this.mv.visitInsn(88);
            }
            else {
                this.mv.visitInsn(87);
            }
        }
        
        private boolean isNonStaticCall(final int opcode) {
            return 184 != opcode;
        }
    }
}
