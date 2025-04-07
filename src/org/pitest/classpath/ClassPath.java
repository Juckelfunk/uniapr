// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.classpath;

import org.pitest.util.Log;
import org.pitest.functional.F;
import org.pitest.functional.predicate.Predicate;
import java.util.Set;
import java.util.LinkedHashSet;
import java.net.URL;
import java.io.InputStream;
import org.pitest.util.StreamUtil;
import java.util.zip.ZipException;
import java.util.Iterator;
import java.io.IOException;
import org.pitest.util.PitError;
import java.util.ArrayList;
import java.util.List;
import org.pitest.functional.FCollection;
import java.io.File;
import java.util.Collection;
import java.util.Arrays;
import java.util.logging.Logger;

public class ClassPath
{
    private static final Logger LOG;
    private final CompoundClassPathRoot root;
    
    public ClassPath() {
        this(getClassPathElementsAsFiles());
    }
    
    public ClassPath(final ClassPathRoot... roots) {
        this(Arrays.asList(roots));
    }
    
    public ClassPath(final Collection<File> files) {
        this(createRoots(FCollection.filter(files, exists())));
    }
    
    public ClassPath(final List<ClassPathRoot> roots) {
        this.root = new CompoundClassPathRoot(roots);
    }
    
    public Collection<String> classNames() {
        return this.root.classNames();
    }
    
    private static List<ClassPathRoot> createRoots(final Collection<File> files) {
        File lastFile = null;
        try {
            final List<ClassPathRoot> rs = new ArrayList<ClassPathRoot>();
            final Iterator<File> iterator = files.iterator();
            while (iterator.hasNext()) {
                final File f = lastFile = iterator.next();
                if (f.isDirectory()) {
                    rs.add(new DirectoryClassPathRoot(f));
                }
                else {
                    handleArchive(rs, f);
                }
            }
            return rs;
        }
        catch (IOException ex) {
            throw new PitError("Error handling file " + lastFile, ex);
        }
    }
    
    private static void handleArchive(final List<ClassPathRoot> rs, final File f) throws IOException {
        try {
            if (!f.canRead()) {
                throw new IOException("Can't read the file " + f);
            }
            rs.add(new ArchiveClassPathRoot(f));
        }
        catch (ZipException ex) {
            ClassPath.LOG.warning("Can't open the archive " + f);
        }
    }
    
    public byte[] getClassData(final String classname) throws IOException {
        try (final InputStream is = this.root.getData(classname)) {
            if (is != null) {
                return StreamUtil.streamToByteArray(is);
            }
            return null;
        }
    }
    
    public URL findResource(final String name) {
        try {
            return this.root.getResource(name);
        }
        catch (IOException exception) {
            return null;
        }
    }
    
    public static Collection<String> getClassPathElementsAsPaths() {
        final Set<String> filesAsString = new LinkedHashSet<String>();
        FCollection.mapTo(getClassPathElementsAsFiles(), fileToString(), filesAsString);
        return filesAsString;
    }
    
    public static Collection<File> getClassPathElementsAsFiles() {
        final Set<File> us = new LinkedHashSet<File>();
        FCollection.mapTo(getClassPathElementsAsAre(), stringToCanonicalFile(), us);
        return us;
    }
    
    public Collection<String> findClasses(final Predicate<String> nameFilter) {
        return (Collection<String>)FCollection.filter(this.classNames(), (F<Object, Boolean>)nameFilter);
    }
    
    public String getLocalClassPath() {
        return this.root.cacheLocation().value();
    }
    
    public ClassPath getComponent(final Predicate<ClassPathRoot> predicate) {
        return new ClassPath((ClassPathRoot[])FCollection.filter(this.root, (F<Object, Boolean>)predicate).toArray(new ClassPathRoot[0]));
    }
    
    private static F<File, Boolean> exists() {
        return new F<File, Boolean>() {
            @Override
            public Boolean apply(final File a) {
                return a.exists() && a.canRead();
            }
        };
    }
    
    private static F<File, String> fileToString() {
        return new F<File, String>() {
            @Override
            public String apply(final File file) {
                return file.getPath();
            }
        };
    }
    
    private static F<String, File> stringToCanonicalFile() {
        return new F<String, File>() {
            @Override
            public File apply(final String fileAsString) {
                try {
                    return new File(fileAsString).getCanonicalFile();
                }
                catch (IOException ex) {
                    throw new PitError("Error transforming classpath element " + fileAsString, ex);
                }
            }
        };
    }
    
    private static List<String> getClassPathElementsAsAre() {
        final String classPath = System.getProperty("java.class.path");
        final String separator = File.pathSeparator;
        if (classPath != null) {
            return Arrays.asList(classPath.split(separator));
        }
        return new ArrayList<String>();
    }
    
    static {
        LOG = Log.getLogger();
    }
}
