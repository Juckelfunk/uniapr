// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.tooling;

import org.pitest.util.PitError;
import java.util.zip.ZipEntry;
import sun.pitest.InvokeReceiver;
import sun.pitest.CodeCoverageStore;
import org.pitest.boot.HotSwapAgent;
import java.io.OutputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.io.IOException;
import org.pitest.util.Unchecked;
import java.io.FileOutputStream;
import java.io.File;
import org.pitest.util.FileUtil;
import org.pitest.classpath.ClassPathByteArraySource;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.functional.Option;
import org.pitest.process.JavaAgent;

public class JarCreatingJarFinder implements JavaAgent
{
    protected static final String CAN_REDEFINE_CLASSES = "Can-Redefine-Classes";
    protected static final String PREMAIN_CLASS = "Premain-Class";
    protected static final String CAN_SET_NATIVE_METHOD = "Can-Set-Native-Method-Prefix";
    protected static final String BOOT_CLASSPATH = "Boot-Class-Path";
    private static final String AGENT_CLASS_NAME;
    private Option<String> location;
    private final ClassByteArraySource classByteSource;
    
    public JarCreatingJarFinder(final ClassByteArraySource classByteSource) {
        this.location = (Option<String>)Option.none();
        this.classByteSource = classByteSource;
    }
    
    public JarCreatingJarFinder() {
        this(new ClassPathByteArraySource());
    }
    
    @Override
    public Option<String> getJarLocation() {
        if (this.location.hasNone()) {
            this.location = this.createJar();
        }
        return this.location;
    }
    
    private Option<String> createJar() {
        try {
            final File randomName = File.createTempFile(FileUtil.randomFilename(), ".jar");
            final FileOutputStream fos = new FileOutputStream(randomName);
            this.createJarFromClassPathResources(fos, randomName.getAbsolutePath());
            return Option.some(randomName.getAbsolutePath());
        }
        catch (IOException ex) {
            throw Unchecked.translateCheckedException(ex);
        }
    }
    
    private void createJarFromClassPathResources(final FileOutputStream fos, final String location) throws IOException {
        final Manifest m = new Manifest();
        m.clear();
        final Attributes global = m.getMainAttributes();
        if (global.getValue(Attributes.Name.MANIFEST_VERSION) == null) {
            global.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        }
        final File mylocation = new File(location);
        global.putValue("Boot-Class-Path", this.getBootClassPath(mylocation));
        global.putValue("Premain-Class", JarCreatingJarFinder.AGENT_CLASS_NAME);
        global.putValue("Can-Redefine-Classes", "true");
        global.putValue("Can-Set-Native-Method-Prefix", "true");
        try (final JarOutputStream jos = new JarOutputStream(fos, m)) {
            this.addClass(HotSwapAgent.class, jos);
            this.addClass(CodeCoverageStore.class, jos);
            this.addClass(InvokeReceiver.class, jos);
        }
    }
    
    private String getBootClassPath(final File mylocation) {
        return mylocation.getAbsolutePath().replace('\\', '/');
    }
    
    private void addClass(final Class<?> clazz, final JarOutputStream jos) throws IOException {
        final String className = clazz.getName();
        final ZipEntry ze = new ZipEntry(className.replace(".", "/") + ".class");
        jos.putNextEntry(ze);
        jos.write(this.classBytes(className));
        jos.closeEntry();
    }
    
    private byte[] classBytes(final String className) {
        final Option<byte[]> bytes = this.classByteSource.getBytes(className);
        if (bytes.hasSome()) {
            return bytes.value();
        }
        throw new PitError("Unable to load class content for " + className);
    }
    
    @Override
    public void close() {
        if (this.location.hasSome()) {
            final File f = new File(this.location.value());
            f.delete();
        }
    }
    
    static {
        AGENT_CLASS_NAME = HotSwapAgent.class.getName();
    }
}
