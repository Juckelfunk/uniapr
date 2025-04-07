// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.classworlds;

import java.util.Enumeration;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

class RealmClassLoader extends URLClassLoader
{
    protected DefaultClassRealm realm;
    
    RealmClassLoader(final DefaultClassRealm realm) {
        this(realm, null);
    }
    
    RealmClassLoader(final DefaultClassRealm realm, final ClassLoader classLoader) {
        super(new URL[0], classLoader);
        this.realm = realm;
    }
    
    DefaultClassRealm getRealm() {
        return this.realm;
    }
    
    void addConstituent(URL constituent) {
        String urlStr = constituent.toExternalForm();
        if (!urlStr.endsWith(".class")) {
            if (urlStr.startsWith("jar:") && urlStr.endsWith("!/")) {
                urlStr = urlStr.substring(4, urlStr.length() - 2);
                try {
                    constituent = new URL(urlStr);
                }
                catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            this.addURL(constituent);
        }
        else {
            try {
                final byte[] b = this.getBytesToEndOfStream(new DataInputStream(constituent.openStream()));
                final int start = urlStr.lastIndexOf("byteclass") + 10;
                final int end = urlStr.lastIndexOf(".class");
                final String className = urlStr.substring(start, end);
                super.defineClass(className, b, 0, b.length);
                this.addURL(constituent);
            }
            catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }
    
    public byte[] getBytesToEndOfStream(final DataInputStream in) throws IOException {
        final int chunkSize = (in.available() > 0) ? in.available() : 2048;
        final byte[] buf = new byte[chunkSize];
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream(chunkSize);
        int count;
        while ((count = in.read(buf)) != -1) {
            byteStream.write(buf, 0, count);
        }
        return byteStream.toByteArray();
    }
    
    Class loadClassDirect(final String name) throws ClassNotFoundException {
        return super.loadClass(name, false);
    }
    
    protected Class loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
        return this.getRealm().loadClass(name);
    }
    
    public URL[] getURLs() {
        return super.getURLs();
    }
    
    public URL findResource(final String name) {
        return super.findResource(name);
    }
    
    public URL getResource(final String name) {
        return this.getRealm().getResource(name);
    }
    
    public URL getResourceDirect(final String name) {
        return super.getResource(name);
    }
    
    public Enumeration findResources(final String name) throws IOException {
        return this.getRealm().findResources(name);
    }
    
    public Enumeration findResourcesDirect(final String name) throws IOException {
        return super.findResources(name);
    }
}
