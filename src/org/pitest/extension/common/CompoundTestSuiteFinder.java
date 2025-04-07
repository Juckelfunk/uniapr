// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.extension.common;

import java.util.Iterator;
import java.util.Collections;
import java.util.List;
import java.util.Collection;
import org.pitest.testapi.TestSuiteFinder;

public final class CompoundTestSuiteFinder implements TestSuiteFinder
{
    private final Collection<TestSuiteFinder> children;
    
    public CompoundTestSuiteFinder(final Collection<TestSuiteFinder> children) {
        this.children = children;
    }
    
    @Override
    public List<Class<?>> apply(final Class<?> a) {
        for (final TestSuiteFinder i : this.children) {
            final List<Class<?>> found = i.apply(a);
            if (!found.isEmpty()) {
                return found;
            }
        }
        return Collections.emptyList();
    }
}
