// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.coverage.execute;

import java.util.concurrent.ConcurrentHashMap;
import org.pitest.reflection.Reflection;
import org.pitest.reloc.asm.ClassWriter;
import org.pitest.reloc.asm.ClassVisitor;
import org.pitest.coverage.CoverageClassVisitor;
import sun.pitest.CodeCoverageStore;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.classinfo.ComputeClassWriter;
import org.pitest.bytecode.FrameOptions;
import org.pitest.classpath.ClassloaderByteArraySource;
import org.pitest.reloc.asm.ClassReader;
import org.pitest.util.StreamUtil;
import java.io.IOException;
import org.pitest.util.Unchecked;
import java.io.ByteArrayInputStream;
import org.pitest.util.IsolationUtils;
import java.io.InputStream;
import java.util.Map;

public final class JavassistCoverageInterceptor
{
    private static final Map<String, String> COMPUTE_CACHE;
    
    private JavassistCoverageInterceptor() {
    }
    
    public static InputStream openClassfile(final Object classPath, final String name) {
        try {
            if (isInstrumentedClass(name)) {
                final byte[] bs = getOriginalBytes(classPath, name);
                return new ByteArrayInputStream(transformBytes(IsolationUtils.getContextClassLoader(), name, bs));
            }
            return returnNormalBytes(classPath, name);
        }
        catch (IOException ex) {
            throw Unchecked.translateCheckedException(ex);
        }
    }
    
    private static byte[] getOriginalBytes(final Object classPath, final String name) throws IOException {
        try (final InputStream is = returnNormalBytes(classPath, name)) {
            final byte[] bs = StreamUtil.streamToByteArray(is);
            return bs;
        }
    }
    
    private static byte[] transformBytes(final ClassLoader loader, final String className, final byte[] classfileBuffer) {
        final ClassReader reader = new ClassReader(classfileBuffer);
        final ClassWriter writer = new ComputeClassWriter(new ClassloaderByteArraySource(loader), JavassistCoverageInterceptor.COMPUTE_CACHE, FrameOptions.pickFlags(classfileBuffer));
        final int id = CodeCoverageStore.registerClass(className);
        reader.accept(new CoverageClassVisitor(id, writer), 8);
        return writer.toByteArray();
    }
    
    private static InputStream returnNormalBytes(final Object classPath, final String name) {
        try {
            return (InputStream)Reflection.publicMethod(classPath.getClass(), "openClassfile").invoke(classPath, name);
        }
        catch (Exception e) {
            throw Unchecked.translateCheckedException(e);
        }
    }
    
    private static boolean isInstrumentedClass(final String name) {
        return true;
    }
    
    static {
        COMPUTE_CACHE = new ConcurrentHashMap<String, String>();
    }
}
