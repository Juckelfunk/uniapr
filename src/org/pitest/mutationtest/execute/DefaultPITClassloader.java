// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.execute;

import java.util.NoSuchElementException;
import java.util.Enumeration;
import java.net.URL;
import java.io.IOException;
import org.pitest.classpath.ClassPath;

public class DefaultPITClassloader extends ClassLoader
{
    private final ClassPath classPath;
    
    public DefaultPITClassloader(final ClassPath cp, final ClassLoader parent) {
        super(parent);
        this.classPath = cp;
    }
    
    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        try {
            final byte[] b = this.classPath.getClassData(name);
            if (b == null) {
                throw new ClassNotFoundException(name);
            }
            this.definePackage(name);
            return this.defineClass(name, b);
        }
        catch (IOException exception) {
            throw new ClassNotFoundException(name, exception);
        }
    }
    
    private void definePackage(final String name) {
        final int i = name.lastIndexOf(46);
        if (i != -1) {
            final String pkgname = name.substring(0, i);
            if (this.getPackage(pkgname) == null) {
                this.definePackage(pkgname, null, null, null, null, null, null, null);
            }
        }
    }
    
    protected Class<?> defineClass(final String name, final byte[] b) {
        return this.defineClass(name, b, 0, b.length);
    }
    
    @Override
    protected URL findResource(final String name) {
        return this.classPath.findResource(name);
    }
    
    @Override
    protected Enumeration<URL> findResources(final String name) {
        return new Enumeration<URL>() {
            private URL element = DefaultPITClassloader.this.findResource(name);
            
            @Override
            public boolean hasMoreElements() {
                return this.element != null;
            }
            
            @Override
            public URL nextElement() {
                if (this.element != null) {
                    final URL next = this.element;
                    this.element = null;
                    return next;
                }
                throw new NoSuchElementException();
            }
        };
    }
}
