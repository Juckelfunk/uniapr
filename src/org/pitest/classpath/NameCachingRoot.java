// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.classpath;

import org.pitest.functional.F;
import org.pitest.functional.Option;
import java.io.IOException;
import org.pitest.classinfo.ClassName;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.lang.ref.SoftReference;

public class NameCachingRoot implements ClassPathRoot
{
    private final ClassPathRoot child;
    private SoftReference<Collection<String>> cache;
    
    public NameCachingRoot(final ClassPathRoot child) {
        this.child = child;
    }
    
    @Override
    public URL getResource(final String name) throws MalformedURLException {
        return this.child.getResource(name);
    }
    
    @Override
    public InputStream getData(final String name) throws IOException {
        final Collection<String> names = this.classNames();
        if (!names.contains(ClassName.fromString(name).asJavaName())) {
            return null;
        }
        return this.child.getData(name);
    }
    
    @Override
    public Collection<String> classNames() {
        if (this.cache != null) {
            final Collection<String> cachedNames = this.cache.get();
            if (cachedNames != null) {
                return cachedNames;
            }
        }
        final Collection<String> names = this.child.classNames();
        this.cache = new SoftReference<Collection<String>>(names);
        return names;
    }
    
    @Override
    public Option<String> cacheLocation() {
        return this.child.cacheLocation();
    }
    
    public static F<ClassPathRoot, ClassPathRoot> toCachingRoot() {
        return new F<ClassPathRoot, ClassPathRoot>() {
            @Override
            public ClassPathRoot apply(final ClassPathRoot a) {
                if (a instanceof IOHeavyRoot) {
                    return new NameCachingRoot(a);
                }
                return a;
            }
        };
    }
}
