// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr.commons.misc;

import java.io.InputStream;
import org.objectweb.asm.ClassReader;
import java.io.FileInputStream;
import org.objectweb.asm.ClassVisitor;
import java.io.File;

public final class MemberNameUtils
{
    private MemberNameUtils() {
    }
    
    public static String sanitizeExtendedTestName(String name) {
        name = name.substring(1 + name.indexOf(32));
        final int indexOfLP = name.indexOf(40);
        if (indexOfLP >= 0) {
            final String testCaseName = name.substring(0, indexOfLP);
            name = name.substring(1 + indexOfLP, name.length() - 1) + "." + testCaseName;
        }
        return name;
    }
    
    public static String sanitizeTestName(String name) {
        name = name.replace(':', '.');
        name = name.replace("..", ".");
        final int indexOfLP = name.indexOf(40);
        if (indexOfLP >= 0) {
            name = name.substring(0, indexOfLP);
        }
        return name;
    }
    
    public static String getClassName(final File classFile) {
        try (final InputStream fis = new FileInputStream(classFile)) {
            final class 1NameExtractor extends ClassVisitor
            {
                String className;
                
                public 1NameExtractor() {
                    super(458752);
                }
                
                @Override
                public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
                    this.className = name.replace('/', '.');
                    super.visit(version, access, name, signature, superName, interfaces);
                }
            }
            final 1NameExtractor extractor = new 1NameExtractor();
            final ClassReader cr = new ClassReader(fis);
            cr.accept(extractor, 0);
            return extractor.className;
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
