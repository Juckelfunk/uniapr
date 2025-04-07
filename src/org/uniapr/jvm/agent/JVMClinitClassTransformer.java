// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr.jvm.agent;

import org.uniapr.jvm.offline.LeakingFieldMain;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.File;
import org.uniapr.jvm.core.CheckPraPRGenVisitor;
import java.lang.instrument.IllegalClassFormatException;
import org.objectweb.asm.ClassWriter;
import org.uniapr.jvm.core.NormalAccessTransformVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassReader;
import org.uniapr.jvm.core.JVMStatus;
import org.uniapr.jvm.core.reflection.ReflectionClassTransformVisitor;
import java.security.ProtectionDomain;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.instrument.ClassFileTransformer;

public class JVMClinitClassTransformer implements ClassFileTransformer
{
    public static Map<String, Boolean> leakingClasses;
    public static Map<String, String> classInheritance;
    public static Map<String, List<String>> leakingClassMap;
    
    public static Map<String, List<String>> prepareLeakingClassMap() {
        final Map<String, List<String>> map = new HashMap<String, List<String>>();
        for (final String clazz : JVMClinitClassTransformer.classInheritance.keySet()) {
            final List<String> ancestors = new ArrayList<String>();
            String current = clazz;
            while (JVMClinitClassTransformer.classInheritance.containsKey(current)) {
                current = JVMClinitClassTransformer.classInheritance.get(current);
                if (JVMClinitClassTransformer.leakingClasses.containsKey(current)) {
                    ancestors.add(0, current);
                }
            }
            if (ancestors.size() > 0) {
                map.put(clazz, ancestors);
            }
        }
        return map;
    }
    
    @Override
    public byte[] transform(final ClassLoader loader, final String slashClassName, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            if (slashClassName == null) {
                return classfileBuffer;
            }
            if (!ReflectionClassTransformVisitor.reflectionSites.containsKey(slashClassName)) {
                if (JVMStatus.isExcluded(slashClassName)) {
                    return classfileBuffer;
                }
                if (loader != ClassLoader.getSystemClassLoader()) {
                    return classfileBuffer;
                }
            }
            byte[] result = classfileBuffer;
            final ClassReader reader = new ClassReader(classfileBuffer);
            if (this.isPraPRGen(result)) {
                return classfileBuffer;
            }
            final ClassWriter writer = new ComputeClassWriter(FrameOptions.pickFlags(classfileBuffer));
            ClassVisitor cv = null;
            if (ReflectionClassTransformVisitor.reflectionSites.containsKey(slashClassName)) {
                cv = new ReflectionClassTransformVisitor(slashClassName, writer, this.isInterface(reader));
            }
            else {
                final int id = JVMStatus.registerClass(slashClassName);
                cv = new NormalAccessTransformVisitor(slashClassName, writer, this.isInterface(reader), id);
            }
            reader.accept(cv, 8);
            result = writer.toByteArray();
            return result;
        }
        catch (Throwable t) {
            t.printStackTrace();
            final String message = "Exception thrown during instrumentation";
            System.err.println(message);
            System.exit(1);
            throw new RuntimeException("Should not be reached");
        }
    }
    
    protected boolean isPraPRGen(final byte[] result) {
        final ClassReader reader = new ClassReader(result);
        final CheckPraPRGenVisitor visitor = new CheckPraPRGenVisitor();
        reader.accept(visitor, 0);
        return visitor.isPraPRGen;
    }
    
    public boolean isInterface(final ClassReader cr) {
        return (cr.getAccess() & 0x200) != 0x0;
    }
    
    protected void logAllClasses(final ClassLoader loader, final String slashClassName, final byte[] result) throws FileNotFoundException, IOException {
        final String path = "fake" + File.separator + slashClassName.substring(0, slashClassName.lastIndexOf("/"));
        final String clazzName = slashClassName.substring(slashClassName.lastIndexOf("/") + 1);
        final File file = new File(path);
        file.mkdirs();
        final FileOutputStream fos = new FileOutputStream(path + File.separator + clazzName + ".class");
        fos.write(result);
        fos.close();
    }
    
    public static void log(final String content) {
        try {
            final BufferedWriter writer = new BufferedWriter(new FileWriter("temp.log", true));
            writer.write(content + "\n");
            writer.flush();
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    static {
        try {
            JVMClinitClassTransformer.leakingClasses = LeakingFieldMain.deSerializeClasses(LeakingFieldMain.CLASSLOG);
            JVMClinitClassTransformer.classInheritance = LeakingFieldMain.deSerializeInheritance(LeakingFieldMain.INHERILOG);
            JVMClinitClassTransformer.leakingClassMap = prepareLeakingClassMap();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
