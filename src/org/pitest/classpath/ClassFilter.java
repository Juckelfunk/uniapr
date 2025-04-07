// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.classpath;

import org.pitest.functional.predicate.Predicate;

public class ClassFilter
{
    private final Predicate<String> test;
    private final Predicate<String> code;
    
    public ClassFilter(final Predicate<String> test, final Predicate<String> code) {
        this.test = test;
        this.code = code;
    }
    
    public Predicate<String> getTest() {
        return this.test;
    }
    
    public Predicate<String> getCode() {
        return this.code;
    }
}
