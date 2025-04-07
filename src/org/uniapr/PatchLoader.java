// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr;

import edu.utdallas.prf.reloc.apache.commons.io.FileUtils;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.io.FileFilter;
import java.io.File;

public final class PatchLoader
{
    private static final String[] CLASS_FILES_EXT;
    private final File rootDir;
    
    public PatchLoader(final File rootDir) {
        this.rootDir = rootDir;
    }
    
    private static FileFilter isFolder() {
        return new FileFilter() {
            @Override
            public boolean accept(final File file) {
                return file.isDirectory();
            }
        };
    }
    
    public List<Patch> loadPatches() throws NoPatchFoundException {
        final File[] filesList = this.rootDir.listFiles(isFolder());
        if (filesList == null || filesList.length == 0) {
            throw new NoPatchFoundException("No patches were found under" + this.rootDir.getAbsolutePath());
        }
        final ArrayList<Patch> patches = new ArrayList<Patch>();
        for (final File patchBaseDir : filesList) {
            final String patchName = patchBaseDir.getName();
            final List<LoadedClass> classes = new ArrayList<LoadedClass>();
            for (final File classFile : listClassFiles(patchBaseDir)) {
                try {
                    classes.add(LoadedClass.fromFile(classFile));
                }
                catch (Exception e) {
                    throw new RuntimeException("Unable to load the class");
                }
            }
            if (!classes.isEmpty()) {
                patches.add(new Patch(classes, (Collection<String>)Collections.emptyList(), patchName));
            }
        }
        patches.trimToSize();
        return patches;
    }
    
    private static Collection<File> listClassFiles(final File baseDir) {
        return FileUtils.listFiles(baseDir, PatchLoader.CLASS_FILES_EXT, true);
    }
    
    static {
        CLASS_FILES_EXT = new String[] { "class" };
    }
}
