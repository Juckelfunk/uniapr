// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.util;

import java.lang.reflect.Constructor;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.Enumeration;
import java.net.URL;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Collection;

public abstract class ServiceLoader
{
    public static <S> Collection<S> load(final Class<S> ifc) {
        return load(ifc, Thread.currentThread().getContextClassLoader());
    }
    
    public static <S> Collection<S> load(final Class<S> ifc, final ClassLoader loader) {
        try {
            return (Collection<S>)loadImpl((Class<Object>)ifc, loader);
        }
        catch (IOException ex) {
            throw new PitError("Error creating service " + ifc.getName(), ex);
        }
    }
    
    private static <S> Collection<S> loadImpl(final Class<S> ifc, final ClassLoader loader) throws IOException {
        final Enumeration<URL> e = loader.getResources("META-INF/services/" + ifc.getName());
        final Collection<S> services = new ArrayList<S>();
        while (e.hasMoreElements()) {
            final URL url = e.nextElement();
            try (final InputStream is = url.openStream()) {
                createServicesFromStream(ifc, loader, services, is);
            }
        }
        return services;
    }
    
    private static <S> void createServicesFromStream(final Class<S> ifc, final ClassLoader loader, final Collection<S> services, final InputStream is) throws IOException {
        final BufferedReader r = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        while (true) {
            String line = r.readLine();
            if (line == null) {
                break;
            }
            final int comment = line.indexOf(35);
            if (comment >= 0) {
                line = line.substring(0, comment);
            }
            final String name = line.trim();
            if (name.length() == 0) {
                continue;
            }
            services.add(createService(name, ifc, loader));
        }
    }
    
    private static <S> S createService(final String name, final Class<S> ifc, final ClassLoader loader) {
        try {
            final Class<?> clz = Class.forName(name, true, loader);
            final Class<? extends S> impl = clz.asSubclass(ifc);
            final Constructor<? extends S> ctor = impl.getConstructor((Class<?>[])new Class[0]);
            return (S)ctor.newInstance(new Object[0]);
        }
        catch (Exception ex) {
            throw new PitError("Error creating service " + ifc.getName(), ex);
        }
    }
}
