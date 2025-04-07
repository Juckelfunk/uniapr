// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.junit;

import org.junit.runner.RunWith;
import java.util.Collections;
import java.util.Arrays;
import org.junit.runners.Suite;
import java.util.List;
import org.pitest.testapi.TestSuiteFinder;

public class JUnit4SuiteFinder implements TestSuiteFinder
{
    @Override
    public List<Class<?>> apply(final Class<?> a) {
        final Suite.SuiteClasses annotation = a.getAnnotation(Suite.SuiteClasses.class);
        if (annotation != null && this.hasSuitableRunnner(a)) {
            return Arrays.asList(annotation.value());
        }
        return Collections.emptyList();
    }
    
    private boolean hasSuitableRunnner(final Class<?> clazz) {
        final RunWith runWith = clazz.getAnnotation(RunWith.class);
        return runWith != null && runWith.value().equals(Suite.class);
    }
}
