// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.junit;

import org.junit.Test;
import java.lang.annotation.Annotation;
import org.junit.runner.RunWith;
import junit.framework.TestSuite;
import junit.framework.TestCase;
import org.pitest.functional.predicate.Predicate;
import org.pitest.functional.Option;
import org.pitest.classinfo.ClassInfo;

abstract class TestInfo
{
    public static boolean isWithinATestClass(final ClassInfo clazz) {
        final Option<ClassInfo> outerClass = clazz.getOuterClass();
        return isATest(clazz) || (outerClass.hasSome() && isATest(outerClass.value()));
    }
    
    private static boolean isATest(final ClassInfo clazz) {
        return isJUnit3Test(clazz) || isJUnit4Test(clazz) || isATest(clazz.getSuperClass());
    }
    
    private static boolean isATest(final Option<ClassInfo> clazz) {
        return clazz.hasSome() && isATest(clazz.value());
    }
    
    public static Predicate<ClassInfo> isATest() {
        return new Predicate<ClassInfo>() {
            @Override
            public Boolean apply(final ClassInfo clazz) {
                return isATest(clazz);
            }
        };
    }
    
    private static boolean isJUnit3Test(final ClassInfo clazz) {
        return clazz.descendsFrom(TestCase.class) || clazz.descendsFrom(TestSuite.class);
    }
    
    private static boolean isJUnit4Test(final ClassInfo clazz) {
        return clazz.hasAnnotation(RunWith.class) || clazz.hasAnnotation(Test.class);
    }
}
