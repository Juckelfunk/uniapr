// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.classpath;

import org.pitest.functional.Option;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public class OtherClassLoaderClassPathRoot implements ClassPathRoot
{
    private final ClassLoader loader;
    
    public OtherClassLoaderClassPathRoot(final ClassLoader loader) {
        this.loader = loader;
    }
    
    @Override
    public Collection<String> classNames() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public InputStream getData(final String name) throws IOException {
        return this.loader.getResourceAsStream(name.replace(".", "/") + ".class");
    }
    
    @Override
    public URL getResource(final String name) throws MalformedURLException {
        return this.loader.getResource(name);
    }
    
    @Override
    public Option<String> cacheLocation() {
        return (Option<String>)Option.none();
    }
}
