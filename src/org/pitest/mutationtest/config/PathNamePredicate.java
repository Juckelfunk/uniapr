// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.config;

import org.pitest.classpath.ClassPathRoot;
import org.pitest.functional.predicate.Predicate;

public class PathNamePredicate implements Predicate<ClassPathRoot>
{
    private final Predicate<String> stringFilter;
    
    public PathNamePredicate(final Predicate<String> stringFilter) {
        this.stringFilter = stringFilter;
    }
    
    @Override
    public Boolean apply(final ClassPathRoot classPathRoot) {
        return this.cacheLocationOptionExists(classPathRoot) && this.cacheLocationMatchesFilter(classPathRoot);
    }
    
    private Boolean cacheLocationMatchesFilter(final ClassPathRoot classPathRoot) {
        final String cacheLocationValue = classPathRoot.cacheLocation().value();
        return this.stringFilter.apply(cacheLocationValue);
    }
    
    private boolean cacheLocationOptionExists(final ClassPathRoot a) {
        return a.cacheLocation().hasSome();
    }
}
