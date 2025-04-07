// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.testng;

import java.lang.reflect.Modifier;
import org.pitest.functional.F;
import org.pitest.functional.FCollection;
import java.lang.annotation.Annotation;
import org.pitest.reflection.IsAnnotatedWith;
import org.pitest.reflection.Reflection;
import org.testng.annotations.Test;
import java.util.Collections;
import org.pitest.testapi.TestUnit;
import java.util.List;
import java.util.Collection;
import org.pitest.testapi.TestGroupConfig;
import org.pitest.testapi.TestUnitFinder;

public class TestNGTestUnitFinder implements TestUnitFinder
{
    private final TestGroupConfig config;
    private final Collection<String> includedTestMethods;
    
    public TestNGTestUnitFinder(final TestGroupConfig config, final Collection<String> includedTestMethods) {
        this.config = config;
        this.includedTestMethods = includedTestMethods;
    }
    
    @Override
    public List<TestUnit> findTestUnits(final Class<?> clazz) {
        if (!this.isAbstract(clazz) && (this.hasClassAnnotation(clazz) || this.hasMethodAnnotation(clazz))) {
            return (List<TestUnit>)Collections.singletonList(new TestNGTestUnit(clazz, this.config, this.includedTestMethods));
        }
        return Collections.emptyList();
    }
    
    private boolean hasClassAnnotation(final Class<?> clazz) {
        return clazz.getAnnotation(Test.class) != null;
    }
    
    private boolean hasMethodAnnotation(final Class<?> clazz) {
        return FCollection.contains(Reflection.allMethods(clazz), (F<Object, Boolean>)IsAnnotatedWith.instance((Class<? extends Annotation>)Test.class));
    }
    
    private boolean isAbstract(final Class<?> clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }
}
