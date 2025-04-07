// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.classpath;

import org.pitest.functional.Option;
import java.util.List;
import java.util.LinkedList;
import java.util.Collection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.File;

public class DirectoryClassPathRoot implements ClassPathRoot, IOHeavyRoot
{
    private final File root;
    
    public DirectoryClassPathRoot(final File root) {
        this.root = root;
    }
    
    @Override
    public InputStream getData(final String classname) throws IOException {
        final String filename = classname.replace('.', File.separatorChar).concat(".class");
        final File file = new File(this.root, filename);
        if (file.canRead()) {
            return new FileInputStream(file);
        }
        return null;
    }
    
    @Override
    public URL getResource(final String name) throws MalformedURLException {
        final File f = new File(this.root, name);
        if (f.canRead()) {
            return f.toURI().toURL();
        }
        return null;
    }
    
    @Override
    public Collection<String> classNames() {
        return this.classNames(this.root);
    }
    
    private Collection<String> classNames(final File file) {
        final List<String> classNames = new LinkedList<String>();
        for (final File f : file.listFiles()) {
            if (f.isDirectory()) {
                classNames.addAll(this.classNames(f));
            }
            else if (f.getName().endsWith(".class")) {
                classNames.add(this.fileToClassName(f));
            }
        }
        return classNames;
    }
    
    private String fileToClassName(final File f) {
        return f.getAbsolutePath().substring(this.root.getAbsolutePath().length() + 1, f.getAbsolutePath().length() - ".class".length()).replace(File.separatorChar, '.');
    }
    
    @Override
    public Option<String> cacheLocation() {
        return Option.some(this.root.getAbsolutePath());
    }
}
