// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.classworlds;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.io.IOException;
import java.util.jar.JarInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UberJarRealmClassLoader extends RealmClassLoader
{
    private Map classIndex;
    private List urls;
    private Map jarIndexes;
    
    public UberJarRealmClassLoader(final DefaultClassRealm realm) {
        super(realm);
        this.urls = new ArrayList();
        this.classIndex = new HashMap();
        this.jarIndexes = new HashMap();
    }
    
    public void addConstituent(final URL constituent) {
        if ("jar".equals(constituent.getProtocol()) || constituent.toExternalForm().endsWith(".jar")) {
            this.buildIndexForJar(constituent);
        }
        this.urls.add(constituent);
        super.addConstituent(constituent);
    }
    
    private void buildIndexForJar(final URL inUrl) {
        final HashMap index = new HashMap();
        String urlText = null;
        if (inUrl.getProtocol().equals("jar")) {
            urlText = inUrl.toExternalForm();
        }
        else {
            urlText = "jar:" + inUrl.toExternalForm();
        }
        URL resourceUrl = null;
        try {
            final JarInputStream in = new JarInputStream(inUrl.openStream());
            try {
                JarEntry entry = null;
                while ((entry = in.getNextJarEntry()) != null) {
                    final String resourceName = entry.getName();
                    resourceUrl = new URL(urlText + "!/" + resourceName);
                    index.put(resourceName, resourceUrl);
                }
            }
            finally {
                in.close();
            }
        }
        catch (IOException ex) {}
        this.jarIndexes.put(inUrl, index);
    }
    
    public Class loadClassDirect(final String className) throws ClassNotFoundException {
        final String classPath = className.replace('.', '/') + ".class";
        if (this.classIndex.containsKey(classPath)) {
            return this.classIndex.get(classPath);
        }
        final Iterator urlIter = this.urls.iterator();
        URL eachUrl = null;
        byte[] classBytes = null;
        while (classBytes == null && urlIter.hasNext()) {
            eachUrl = urlIter.next();
            if ("jar".equals(eachUrl.getProtocol()) || eachUrl.toExternalForm().endsWith(".jar")) {
                classBytes = this.findClassInJarStream(eachUrl, classPath);
            }
            else {
                classBytes = this.findClassInDirectoryUrl(eachUrl, classPath);
            }
        }
        if (classBytes == null) {
            return null;
        }
        final Class cls = this.defineClass(className, classBytes, 0, classBytes.length);
        this.classIndex.put(classPath, cls);
        return cls;
    }
    
    public URL findResource(final String name) {
        URL resourceUrl = null;
        final Iterator urlIter = this.urls.iterator();
        URL eachUrl = null;
        while (urlIter.hasNext()) {
            eachUrl = urlIter.next();
            if ("jar".equals(eachUrl.getProtocol()) || eachUrl.toExternalForm().endsWith(".jar")) {
                resourceUrl = this.findResourceInJarStream(eachUrl, name);
            }
            else {
                resourceUrl = this.findResourceInDirectoryUrl(eachUrl, name);
            }
            if (resourceUrl != null) {
                return resourceUrl;
            }
        }
        return null;
    }
    
    public Enumeration findResourcesDirect(final String name) {
        final Vector list = new Vector();
        URL resourceUrl = null;
        final Iterator urlIter = this.urls.iterator();
        URL eachUrl = null;
        while (urlIter.hasNext()) {
            eachUrl = urlIter.next();
            if ("jar".equals(eachUrl.getProtocol()) || eachUrl.toExternalForm().endsWith(".jar")) {
                resourceUrl = this.findResourceInJarStream(eachUrl, name);
            }
            else {
                resourceUrl = this.findResourceInDirectoryUrl(eachUrl, name);
            }
            if (resourceUrl != null) {
                list.add(resourceUrl);
            }
        }
        return list.elements();
    }
    
    protected URL findResourceInJarStream(final URL inUrl, final String path) {
        return this.jarIndexes.get(inUrl).get(path);
    }
    
    protected URL findResourceInDirectoryUrl(final URL inUrl, final String path) {
        return null;
    }
    
    protected byte[] findClassInJarStream(final URL inUrl, final String path) {
        final URL classUrl = this.jarIndexes.get(inUrl).get(path);
        if (classUrl != null) {
            try {
                return this.readStream(classUrl.openStream());
            }
            catch (IOException ex) {}
        }
        return null;
    }
    
    protected byte[] findClassInDirectoryUrl(final URL url, final String path) {
        try {
            final URL classUrl = new URL(url, path);
        }
        catch (IOException ex) {}
        return null;
    }
    
    private byte[] readStream(final InputStream in) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            final byte[] buffer = new byte[2048];
            int read = 0;
            while (in.available() > 0) {
                read = in.read(buffer, 0, buffer.length);
                if (read < 0) {
                    break;
                }
                out.write(buffer, 0, read);
            }
            return out.toByteArray();
        }
        finally {
            out.close();
        }
    }
}
