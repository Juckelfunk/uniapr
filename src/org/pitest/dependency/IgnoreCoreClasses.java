// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.dependency;

import org.pitest.functional.F;
import org.pitest.functional.prelude.Prelude;
import org.pitest.util.Glob;
import java.util.Arrays;
import java.util.Collection;
import org.pitest.functional.predicate.Predicate;

public class IgnoreCoreClasses implements Predicate<DependencyAccess>
{
    private final Predicate<String> impl;
    private final Collection<String> filtered;
    
    IgnoreCoreClasses() {
        this.filtered = Arrays.asList("java.*", "sun.*", "javax.*", "org.junit.*", "junit.*", "org.mockito.*", "org.powermock.*", "org.jmock.*", "com.sun.*");
        this.impl = (Predicate<String>)Prelude.not((F<Object, Boolean>)Prelude.or(Glob.toGlobPredicates(this.filtered)));
    }
    
    @Override
    public Boolean apply(final DependencyAccess a) {
        final String owner = a.getDest().getOwner().replace("/", ".");
        return this.impl.apply(owner);
    }
}
