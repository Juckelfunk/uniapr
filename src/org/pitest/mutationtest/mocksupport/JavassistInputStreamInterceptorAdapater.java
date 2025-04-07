// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.mocksupport;

import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.ClassWriter;
import org.pitest.functional.F;
import org.pitest.reloc.asm.ClassVisitor;

public class JavassistInputStreamInterceptorAdapater extends ClassVisitor
{
    private final String interceptorClass;
    
    public JavassistInputStreamInterceptorAdapater(final ClassVisitor arg0, final Class<?> interceptor) {
        super(393216, arg0);
        this.interceptorClass = classToName(interceptor);
    }
    
    public static F<ClassWriter, ClassVisitor> inputStreamAdapterSupplier(final Class<?> interceptor) {
        return new F<ClassWriter, ClassVisitor>() {
            @Override
            public ClassVisitor apply(final ClassWriter a) {
                return new JavassistInputStreamInterceptorAdapater(a, interceptor);
            }
        };
    }
    
    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        return new JavassistInputStreamInterceptorMethodVisitor(this.cv.visitMethod(access, name, desc, signature, exceptions), this.interceptorClass);
    }
    
    private static String classToName(final Class<?> clazz) {
        return clazz.getName().replace(".", "/");
    }
}
