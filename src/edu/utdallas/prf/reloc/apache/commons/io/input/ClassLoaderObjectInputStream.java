// 
// Decompiled by Procyon v0.5.36
// 

package edu.utdallas.prf.reloc.apache.commons.io.input;

import java.lang.reflect.Proxy;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class ClassLoaderObjectInputStream extends ObjectInputStream
{
    private final ClassLoader classLoader;
    
    public ClassLoaderObjectInputStream(final ClassLoader classLoader, final InputStream inputStream) throws IOException, StreamCorruptedException {
        super(inputStream);
        this.classLoader = classLoader;
    }
    
    @Override
    protected Class<?> resolveClass(final ObjectStreamClass objectStreamClass) throws IOException, ClassNotFoundException {
        try {
            return Class.forName(objectStreamClass.getName(), false, this.classLoader);
        }
        catch (ClassNotFoundException cnfe) {
            return super.resolveClass(objectStreamClass);
        }
    }
    
    @Override
    protected Class<?> resolveProxyClass(final String[] interfaces) throws IOException, ClassNotFoundException {
        final Class<?>[] interfaceClasses = (Class<?>[])new Class[interfaces.length];
        for (int i = 0; i < interfaces.length; ++i) {
            interfaceClasses[i] = Class.forName(interfaces[i], false, this.classLoader);
        }
        try {
            return Proxy.getProxyClass(this.classLoader, interfaceClasses);
        }
        catch (IllegalArgumentException e) {
            return super.resolveProxyClass(interfaces);
        }
    }
}
