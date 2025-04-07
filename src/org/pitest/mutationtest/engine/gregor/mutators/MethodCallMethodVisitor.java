// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.mutators;

import java.util.HashMap;
import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import org.pitest.functional.F2;
import org.pitest.reloc.asm.Type;
import java.util.Map;
import org.pitest.reloc.asm.MethodVisitor;

class MethodCallMethodVisitor extends MethodVisitor
{
    private static final Map<Type, Integer> RETURN_TYPE_MAP;
    private final F2<String, String, Boolean> filter;
    private final MethodMutatorFactory factory;
    private final MutationContext context;
    private final MethodInfo methodInfo;
    
    MethodCallMethodVisitor(final MethodInfo methodInfo, final MutationContext context, final MethodVisitor writer, final MethodMutatorFactory factory, final F2<String, String, Boolean> filter) {
        super(393216, writer);
        this.factory = factory;
        this.filter = filter;
        this.context = context;
        this.methodInfo = methodInfo;
    }
    
    @Override
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
        if (!this.filter.apply(name, desc) || this.isCallToSuperOrOwnConstructor(name, owner)) {
            this.mv.visitMethodInsn(opcode, owner, name, desc, itf);
        }
        else {
            final MutationIdentifier newId = this.context.registerMutation(this.factory, "removed call to " + owner + "::" + name);
            if (this.context.shouldMutate(newId)) {
                this.popStack(desc, name);
                this.popThisIfNotStatic(opcode);
                this.putReturnValueOnStack(desc, name);
            }
            else {
                this.mv.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }
    }
    
    private boolean isCallToSuperOrOwnConstructor(final String name, final String owner) {
        return this.methodInfo.isConstructor() && MethodInfo.isConstructor(name) && (owner.equals(this.context.getClassInfo().getName()) || this.context.getClassInfo().getSuperName().equals(owner));
    }
    
    private void popThisIfNotStatic(final int opcode) {
        if (!isStatic(opcode)) {
            this.mv.visitInsn(87);
        }
    }
    
    private void popStack(final String desc, final String name) {
        final Type[] argTypes = Type.getArgumentTypes(desc);
        for (int i = argTypes.length - 1; i >= 0; --i) {
            final Type argumentType = argTypes[i];
            if (argumentType.getSize() != 1) {
                this.mv.visitInsn(88);
            }
            else {
                this.mv.visitInsn(87);
            }
        }
        if (MethodInfo.isConstructor(name)) {
            this.mv.visitInsn(87);
        }
    }
    
    private static boolean isStatic(final int opcode) {
        return 184 == opcode;
    }
    
    private void putReturnValueOnStack(final String desc, final String name) {
        final Type returnType = Type.getReturnType(desc);
        if (!returnType.equals(Type.VOID_TYPE)) {
            final Integer opCode = MethodCallMethodVisitor.RETURN_TYPE_MAP.get(returnType);
            if (opCode == null) {
                this.mv.visitInsn(1);
            }
            else {
                this.mv.visitInsn(opCode);
            }
        }
        else if (MethodInfo.isConstructor(name)) {
            this.mv.visitInsn(1);
        }
    }
    
    static {
        (RETURN_TYPE_MAP = new HashMap<Type, Integer>()).put(Type.INT_TYPE, 3);
        MethodCallMethodVisitor.RETURN_TYPE_MAP.put(Type.BOOLEAN_TYPE, 3);
        MethodCallMethodVisitor.RETURN_TYPE_MAP.put(Type.BYTE_TYPE, 3);
        MethodCallMethodVisitor.RETURN_TYPE_MAP.put(Type.CHAR_TYPE, 3);
        MethodCallMethodVisitor.RETURN_TYPE_MAP.put(Type.SHORT_TYPE, 3);
        MethodCallMethodVisitor.RETURN_TYPE_MAP.put(Type.LONG_TYPE, 9);
        MethodCallMethodVisitor.RETURN_TYPE_MAP.put(Type.FLOAT_TYPE, 11);
        MethodCallMethodVisitor.RETURN_TYPE_MAP.put(Type.DOUBLE_TYPE, 14);
    }
}
