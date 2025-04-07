// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.config;

import org.pitest.classpath.ClassPathRoot;
import org.pitest.functional.predicate.Predicate;

public class DefaultDependencyPathPredicate implements Predicate<ClassPathRoot>
{
    @Override
    public Boolean apply(final ClassPathRoot a) {
        return a.cacheLocation().hasSome() && this.isADependencyPath(a.cacheLocation().value());
    }
    
    private boolean isADependencyPath(final String path) {
        final String lowerCasePath = path.toLowerCase();
        return lowerCasePath.endsWith(".jar") || lowerCasePath.endsWith(".zip");
    }
}
