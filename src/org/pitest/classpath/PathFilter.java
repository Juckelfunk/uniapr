// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.classpath;

import org.pitest.functional.predicate.Predicate;

public class PathFilter
{
    private final Predicate<ClassPathRoot> codeFilter;
    private final Predicate<ClassPathRoot> testFilter;
    
    public PathFilter(final Predicate<ClassPathRoot> codeFilter, final Predicate<ClassPathRoot> testFilter) {
        this.codeFilter = codeFilter;
        this.testFilter = testFilter;
    }
    
    public Predicate<ClassPathRoot> getCodeFilter() {
        return this.codeFilter;
    }
    
    public Predicate<ClassPathRoot> getTestFilter() {
        return this.testFilter;
    }
}
