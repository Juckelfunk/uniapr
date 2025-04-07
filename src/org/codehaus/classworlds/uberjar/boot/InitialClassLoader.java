// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.classworlds.uberjar.boot;

import java.util.jar.JarEntry;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.jar.JarInputStream;
import java.util.HashMap;
import java.net.URL;
import java.util.Map;
import java.security.SecureClassLoader;

public class InitialClassLoader extends SecureClassLoader
{
    private Map index;
    private URL classworldsJarUrl;
    
    public InitialClassLoader() throws Exception {
        this.index = new HashMap();
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        final URL classUrl = this.getClass().getResource("InitialClassLoader.class");
        final String urlText = classUrl.toExternalForm();
        final int bangLoc = urlText.indexOf("!");
        System.setProperty("classworlds.lib", urlText.substring(0, bangLoc) + "!/WORLDS-INF/lib");
        this.classworldsJarUrl = new URL(urlText.substring(0, bangLoc) + "!/WORLDS-INF/classworlds.jar");
    }
    
    public synchronized Class findClass(final String className) throws ClassNotFoundException {
        final String classPath = className.replace('.', '/') + ".class";
        if (this.index.containsKey(classPath)) {
            return this.index.get(classPath);
        }
        try {
            final JarInputStream in = new JarInputStream(this.classworldsJarUrl.openStream());
            try {
                JarEntry entry = null;
                while ((entry = in.getNextJarEntry()) != null) {
                    if (entry.getName().equals(classPath)) {
                        final ByteArrayOutputStream out = new ByteArrayOutputStream();
                        try {
                            byte[] buffer = new byte[2048];
                            int read = 0;
                            while (in.available() > 0) {
                                read = in.read(buffer, 0, buffer.length);
                                if (read < 0) {
                                    break;
                                }
                                out.write(buffer, 0, read);
                            }
                            buffer = out.toByteArray();
                            final Class cls = this.defineClass(className, buffer, 0, buffer.length);
                            this.index.put(className, cls);
                            return cls;
                        }
                        finally {
                            out.close();
                        }
                        break;
                    }
                }
            }
            finally {
                in.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new ClassNotFoundException("io error reading stream for: " + className);
        }
        throw new ClassNotFoundException(className);
    }
}
