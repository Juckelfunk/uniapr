// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.mocksupport;

import java.lang.instrument.IllegalClassFormatException;
import org.pitest.bytecode.FrameOptions;
import org.pitest.reloc.asm.ClassReader;
import java.security.ProtectionDomain;
import org.pitest.reloc.asm.ClassVisitor;
import org.pitest.reloc.asm.ClassWriter;
import org.pitest.functional.F;
import org.pitest.functional.predicate.Predicate;
import java.lang.instrument.ClassFileTransformer;

public class BendJavassistToMyWillTransformer implements ClassFileTransformer
{
    private final Predicate<String> filter;
    private final F<ClassWriter, ClassVisitor> transformation;
    
    public BendJavassistToMyWillTransformer(final Predicate<String> filter, final F<ClassWriter, ClassVisitor> transformation) {
        this.filter = filter;
        this.transformation = transformation;
    }
    
    @Override
    public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws IllegalClassFormatException {
        if (this.shouldInclude(className)) {
            final ClassReader reader = new ClassReader(classfileBuffer);
            final ClassWriter writer = new ClassWriter(FrameOptions.pickFlags(classfileBuffer));
            reader.accept(this.transformation.apply(writer), 8);
            return writer.toByteArray();
        }
        return null;
    }
    
    private boolean shouldInclude(final String className) {
        return this.filter.apply(className);
    }
}
