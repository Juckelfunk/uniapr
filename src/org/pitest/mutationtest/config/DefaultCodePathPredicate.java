// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.config;

import org.pitest.classpath.ClassPathRoot;
import org.pitest.functional.predicate.Predicate;

public class DefaultCodePathPredicate implements Predicate<ClassPathRoot>
{
    @Override
    public Boolean apply(final ClassPathRoot a) {
        return a.cacheLocation().hasSome() && !this.isATestPath(a.cacheLocation().value()) && !this.isADependencyPath(a.cacheLocation().value());
    }
    
    private boolean isADependencyPath(final String path) {
        final String lowerCasePath = path.toLowerCase();
        return lowerCasePath.endsWith(".jar") || lowerCasePath.endsWith(".zip");
    }
    
    private boolean isATestPath(final String path) {
        return path.endsWith("test-classes") || path.endsWith("bin-test");
    }
}
