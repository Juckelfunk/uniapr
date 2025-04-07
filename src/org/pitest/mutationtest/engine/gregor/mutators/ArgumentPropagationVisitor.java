// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.mutators;

import java.util.Arrays;
import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.reloc.asm.Type;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import org.pitest.reloc.asm.MethodVisitor;

class ArgumentPropagationVisitor extends MethodVisitor
{
    private final MethodMutatorFactory factory;
    private final MutationContext context;
    
    ArgumentPropagationVisitor(final MutationContext context, final MethodVisitor writer, final MethodMutatorFactory factory) {
        super(393216, writer);
        this.factory = factory;
        this.context = context;
    }
    
    @Override
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
        if (this.hasArgumentMatchingTheReturnType(desc)) {
            final MutationIdentifier newId = this.context.registerMutation(this.factory, "replaced call to " + owner + "::" + name + " with argument");
            if (this.context.shouldMutate(newId)) {
                final Type returnType = Type.getReturnType(desc);
                this.replaceMethodCallWithArgumentHavingSameTypeAsReturnValue(Type.getArgumentTypes(desc), returnType, opcode);
            }
            else {
                this.mv.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }
        else {
            this.mv.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }
    
    private boolean hasArgumentMatchingTheReturnType(final String desc) {
        return this.findLastIndexOfArgumentWithSameTypeAsReturnValue(Type.getArgumentTypes(desc), Type.getReturnType(desc)) > -1;
    }
    
    private void replaceMethodCallWithArgumentHavingSameTypeAsReturnValue(final Type[] argTypes, final Type returnType, final int opcode) {
        final int indexOfPropagatedArgument = this.findLastIndexOfArgumentWithSameTypeAsReturnValue(argTypes, returnType);
        this.popArgumentsBeforePropagatedArgument(argTypes, indexOfPropagatedArgument);
        this.popArgumentsFollowingThePropagated(argTypes, returnType, indexOfPropagatedArgument);
        this.removeThisFromStackIfNotStatic(returnType, opcode);
    }
    
    private int findLastIndexOfArgumentWithSameTypeAsReturnValue(final Type[] argTypes, final Type returnType) {
        return Arrays.asList(argTypes).lastIndexOf(returnType);
    }
    
    private void popArgumentsBeforePropagatedArgument(final Type[] argTypes, final int indexOfPropagatedArgument) {
        final Type[] argumentTypesBeforeNewReturnValue = Arrays.copyOfRange(argTypes, indexOfPropagatedArgument + 1, argTypes.length);
        this.popArguments(argumentTypesBeforeNewReturnValue);
    }
    
    private void popArguments(final Type[] argumentTypes) {
        for (int i = argumentTypes.length - 1; i >= 0; --i) {
            this.popArgument(argumentTypes[i]);
        }
    }
    
    private void popArgumentsFollowingThePropagated(final Type[] argTypes, final Type returnType, final int indexOfPropagatedArgument) {
        final Type[] argsFollowing = Arrays.copyOfRange(argTypes, 0, indexOfPropagatedArgument);
        for (int j = argsFollowing.length - 1; j >= 0; --j) {
            swap(this.mv, returnType, argsFollowing[j]);
            this.popArgument(argsFollowing[j]);
        }
    }
    
    private void removeThisFromStackIfNotStatic(final Type returnType, final int opcode) {
        if (isNotStatic(opcode)) {
            swap(this.mv, returnType, Type.getType(Object.class));
            this.mv.visitInsn(87);
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
    
    private static boolean isNotStatic(final int opcode) {
        return 184 != opcode;
    }
    
    private static void swap(final MethodVisitor mv, final Type stackTop, final Type belowTop) {
        if (stackTop.getSize() == 1) {
            if (belowTop.getSize() == 1) {
                mv.visitInsn(95);
            }
            else {
                mv.visitInsn(91);
                mv.visitInsn(87);
            }
        }
        else {
            if (belowTop.getSize() == 1) {
                mv.visitInsn(93);
            }
            else {
                mv.visitInsn(94);
            }
            mv.visitInsn(88);
        }
    }
}
