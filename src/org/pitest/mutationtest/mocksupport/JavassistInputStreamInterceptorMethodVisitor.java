// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.mocksupport;

import org.pitest.reloc.asm.MethodVisitor;

class JavassistInputStreamInterceptorMethodVisitor extends MethodVisitor
{
    private final String interceptorClass;
    
    JavassistInputStreamInterceptorMethodVisitor(final MethodVisitor mv, final String interceptor) {
        super(393216, mv);
        this.interceptorClass = interceptor;
    }
    
    @Override
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
        if (opcode == 185 && owner.equals("javassist/ClassPath") && name.equals("openClassfile")) {
            this.mv.visitMethodInsn(184, this.interceptorClass, name, "(Ljava/lang/Object;Ljava/lang/String;)Ljava/io/InputStream;", false);
        }
        else {
            this.mv.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }
}
