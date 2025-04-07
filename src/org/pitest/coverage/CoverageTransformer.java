// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.coverage;

import org.pitest.reloc.asm.ClassWriter;
import org.pitest.reloc.asm.ClassVisitor;
import sun.pitest.CodeCoverageStore;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.classinfo.ComputeClassWriter;
import org.pitest.bytecode.FrameOptions;
import org.pitest.classpath.ClassloaderByteArraySource;
import org.pitest.reloc.asm.ClassReader;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.pitest.functional.predicate.Predicate;
import java.lang.instrument.ClassFileTransformer;

public class CoverageTransformer implements ClassFileTransformer
{
    private final Predicate<String> filter;
    private final Map<String, String> computeCache;
    
    public CoverageTransformer(final Predicate<String> filter) {
        this.computeCache = new ConcurrentHashMap<String, String>();
        this.filter = filter;
    }
    
    @Override
    public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws IllegalClassFormatException {
        final boolean include = this.shouldInclude(className);
        if (include) {
            try {
                return this.transformBytes(this.pickLoader(loader), className, classfileBuffer);
            }
            catch (RuntimeException t) {
                System.err.println("RuntimeException while transforming  " + className);
                t.printStackTrace();
                throw t;
            }
        }
        return null;
    }
    
    private byte[] transformBytes(final ClassLoader loader, final String className, final byte[] classfileBuffer) {
        final ClassReader reader = new ClassReader(classfileBuffer);
        final ClassWriter writer = new ComputeClassWriter(new ClassloaderByteArraySource(loader), this.computeCache, FrameOptions.pickFlags(classfileBuffer));
        final int id = CodeCoverageStore.registerClass(className);
        reader.accept(new CoverageClassVisitor(id, writer), 8);
        return writer.toByteArray();
    }
    
    private boolean shouldInclude(final String className) {
        return this.filter.apply(className);
    }
    
    private ClassLoader pickLoader(final ClassLoader loader) {
        if (loader != null) {
            return loader;
        }
        return ClassLoader.getSystemClassLoader();
    }
}
