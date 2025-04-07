// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.mocksupport;

import org.pitest.classinfo.ClassName;
import org.pitest.util.Unchecked;
import org.pitest.reflection.Reflection;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.pitest.mutationtest.engine.Mutant;

public final class JavassistInterceptor
{
    private static Mutant mutant;
    
    private JavassistInterceptor() {
    }
    
    public static InputStream openClassfile(final Object classPath, final String name) {
        if (isMutatedClass(name)) {
            final ByteArrayInputStream bais = new ByteArrayInputStream(JavassistInterceptor.mutant.getBytes());
            return bais;
        }
        return returnNormalBytes(classPath, name);
    }
    
    private static InputStream returnNormalBytes(final Object classPath, final String name) {
        try {
            return (InputStream)Reflection.publicMethod(classPath.getClass(), "openClassfile").invoke(classPath, name);
        }
        catch (Exception e) {
            throw Unchecked.translateCheckedException(e);
        }
    }
    
    private static boolean isMutatedClass(final String name) {
        return JavassistInterceptor.mutant != null && JavassistInterceptor.mutant.getDetails().getClassName().equals(ClassName.fromString(name));
    }
    
    public static void setMutant(final Mutant newMutant) {
        JavassistInterceptor.mutant = newMutant;
    }
}
