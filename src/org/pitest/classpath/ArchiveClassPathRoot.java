// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.classpath;

import org.pitest.functional.Option;
import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import org.pitest.util.Unchecked;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.pitest.util.StreamUtil;
import java.io.InputStream;
import java.io.File;

public class ArchiveClassPathRoot implements ClassPathRoot, IOHeavyRoot
{
    private final File file;
    
    public ArchiveClassPathRoot(final File file) {
        this.file = file;
    }
    
    @Override
    public InputStream getData(final String name) throws IOException {
        try (final ZipFile zip = this.getRoot()) {
            final ZipEntry entry = zip.getEntry(name.replace('.', '/') + ".class");
            if (entry == null) {
                return null;
            }
            return StreamUtil.copyStream(zip.getInputStream(entry));
        }
    }
    
    @Override
    public URL getResource(final String name) throws MalformedURLException {
        final ZipFile zip = this.getRoot();
        try {
            final ZipEntry entry = zip.getEntry(name);
            if (entry != null) {
                return new URL("jar:file:" + zip.getName() + "!/" + entry.getName());
            }
            return null;
        }
        finally {
            closeQuietly(zip);
        }
    }
    
    private static void closeQuietly(final ZipFile zip) {
        try {
            zip.close();
        }
        catch (IOException e) {
            throw Unchecked.translateCheckedException(e);
        }
    }
    
    @Override
    public String toString() {
        return "ArchiveClassPathRoot [file=" + this.file.getName() + "]";
    }
    
    @Override
    public Collection<String> classNames() {
        final List<String> names = new ArrayList<String>();
        final ZipFile root = this.getRoot();
        try {
            final Enumeration<? extends ZipEntry> entries = root.entries();
            while (entries.hasMoreElements()) {
                final ZipEntry entry = (ZipEntry)entries.nextElement();
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    names.add(this.stringToClassName(entry.getName()));
                }
            }
            return names;
        }
        finally {
            closeQuietly(root);
        }
    }
    
    private String stringToClassName(final String name) {
        return name.substring(0, name.length() - ".class".length()).replace('/', '.');
    }
    
    @Override
    public Option<String> cacheLocation() {
        return Option.some(this.file.getAbsolutePath());
    }
    
    private ZipFile getRoot() {
        try {
            return new ZipFile(this.file);
        }
        catch (IOException ex) {
            throw Unchecked.translateCheckedException(ex.getMessage() + " (" + this.file + ")", ex);
        }
    }
}
