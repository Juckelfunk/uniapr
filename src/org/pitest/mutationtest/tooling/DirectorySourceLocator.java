// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.tooling;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import org.pitest.functional.FCollection;
import java.util.Collection;
import java.io.Reader;
import org.pitest.functional.Option;
import org.pitest.functional.F;
import java.io.File;
import org.pitest.mutationtest.SourceLocator;

public class DirectorySourceLocator implements SourceLocator
{
    private final File root;
    private final F<File, Option<Reader>> fileToReader;
    
    DirectorySourceLocator(final File root, final F<File, Option<Reader>> fileToReader) {
        this.root = root;
        this.fileToReader = fileToReader;
    }
    
    public DirectorySourceLocator(final File root) {
        this(root, new FileToReader());
    }
    
    @Override
    public Option<Reader> locate(final Collection<String> classes, final String fileName) {
        final List<Reader> matches = (List<Reader>)FCollection.flatMap((Iterable<? extends String>)classes, (F<String, ? extends Iterable<Object>>)this.classNameToSourceFileReader(fileName));
        if (matches.isEmpty()) {
            return (Option<Reader>)Option.none();
        }
        return Option.some(matches.iterator().next());
    }
    
    private F<String, Iterable<Reader>> classNameToSourceFileReader(final String fileName) {
        return new F<String, Iterable<Reader>>() {
            @Override
            public Iterable<Reader> apply(final String className) {
                if (className.contains(".")) {
                    final File f = new File(className.replace(".", File.separator));
                    return DirectorySourceLocator.this.locate(f.getParent() + File.separator + fileName);
                }
                return DirectorySourceLocator.this.locate(fileName);
            }
        };
    }
    
    private Option<Reader> locate(final String fileName) {
        final File f = new File(this.root + File.separator + fileName);
        return this.fileToReader.apply(f);
    }
    
    private static class FileToReader implements F<File, Option<Reader>>
    {
        @Override
        public Option<Reader> apply(final File f) {
            if (f.exists()) {
                try {
                    return (Option<Reader>)Option.some(new FileReader(f));
                }
                catch (FileNotFoundException e) {
                    return (Option<Reader>)Option.none();
                }
            }
            return (Option<Reader>)Option.none();
        }
    }
}
