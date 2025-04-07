// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.tooling;

import java.util.Iterator;
import java.io.Reader;
import org.pitest.functional.Option;
import org.pitest.functional.FArray;
import org.pitest.functional.F;
import org.pitest.functional.FCollection;
import java.io.File;
import java.util.Collection;
import org.pitest.mutationtest.SourceLocator;

public class SmartSourceLocator implements SourceLocator
{
    private static final int MAX_DEPTH = 4;
    private final Collection<SourceLocator> children;
    
    public SmartSourceLocator(final Collection<File> roots) {
        final Collection<File> childDirs = (Collection<File>)FCollection.flatMap((Iterable<? extends File>)roots, (F<File, ? extends Iterable<Object>>)this.collectChildren(0));
        childDirs.addAll(roots);
        final F<File, SourceLocator> fileToSourceLocator = new F<File, SourceLocator>() {
            @Override
            public SourceLocator apply(final File a) {
                return new DirectorySourceLocator(a);
            }
        };
        this.children = FCollection.map(childDirs, fileToSourceLocator);
    }
    
    private F<File, Collection<File>> collectChildren(final int depth) {
        return new F<File, Collection<File>>() {
            @Override
            public Collection<File> apply(final File a) {
                return SmartSourceLocator.this.collectDirectories(a, depth);
            }
        };
    }
    
    private Collection<File> collectDirectories(final File root, final int depth) {
        final Collection<File> childDirs = listFirstLevelDirectories(root);
        if (depth < 4) {
            childDirs.addAll((Collection<? extends File>)FCollection.flatMap((Iterable<? extends File>)childDirs, (F<File, ? extends Iterable<Object>>)this.collectChildren(depth + 1)));
        }
        return childDirs;
    }
    
    private static Collection<File> listFirstLevelDirectories(final File root) {
        final F<File, Boolean> p = new F<File, Boolean>() {
            @Override
            public Boolean apply(final File a) {
                return a.isDirectory();
            }
        };
        return FArray.filter(root.listFiles(), p);
    }
    
    @Override
    public Option<Reader> locate(final Collection<String> classes, final String fileName) {
        for (final SourceLocator each : this.children) {
            final Option<Reader> reader = each.locate(classes, fileName);
            if (reader.hasSome()) {
                return reader;
            }
        }
        return (Option<Reader>)Option.none();
    }
}
