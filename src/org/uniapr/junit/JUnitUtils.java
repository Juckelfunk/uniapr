// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr.junit;

import java.util.HashMap;
import org.pitest.junit.adapter.AdaptedJUnitTestUnit;
import org.pitest.functional.Option;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.Description;
import org.junit.Test;
import java.lang.reflect.Modifier;
import junit.framework.TestCase;
import org.uniapr.jvm.core.JVMStatus;
import org.uniapr.commons.misc.MemberNameUtils;
import java.util.HashSet;
import java.util.Iterator;
import org.pitest.functional.F;
import org.pitest.functional.FCollection;
import org.pitest.classinfo.ClassName;
import java.util.LinkedList;
import org.pitest.testapi.TestUnit;
import java.util.List;
import java.util.Collection;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.Map;

public class JUnitUtils
{
    private static final Map<Class<?>, Set<Method>> VISITED;
    
    public static List<TestUnit> discoverTestUnits(final Collection<String> classNames) {
        final List<TestUnit> testUnits = new LinkedList<TestUnit>();
        final Collection<Class<?>> classes = (Collection<Class<?>>)FCollection.flatMap((Iterable<? extends ClassName>)FCollection.map(classNames, ClassName.stringToClassName()), (F<ClassName, ? extends Iterable<Object>>)ClassName.nameToClass());
        testUnits.addAll(findJUnit4YYTestUnits(classes));
        testUnits.addAll(findJUnit3XXTestUnits(classes));
        excludeTests(testUnits);
        for (final Map.Entry<Class<?>, Set<Method>> entry : JUnitUtils.VISITED.entrySet()) {
            entry.getValue().clear();
        }
        JUnitUtils.VISITED.clear();
        return testUnits;
    }
    
    protected static void excludeTests(final List<TestUnit> testUnits) {
        final Set<TestUnit> set = new HashSet<TestUnit>();
        for (final TestUnit test : testUnits) {
            final String testName = MemberNameUtils.sanitizeExtendedTestName(test.getDescription().getName());
            for (final String prefix : JVMStatus.excludedTestPrefixes) {
                if (testName.startsWith(prefix)) {
                    set.add(test);
                }
            }
        }
        testUnits.removeAll(set);
    }
    
    private static boolean shouldAdd(final Class<?> testSuite, final Method testCase) {
        Set<Method> methods = JUnitUtils.VISITED.get(testSuite);
        if (methods == null) {
            methods = new HashSet<Method>();
            JUnitUtils.VISITED.put(testSuite, methods);
        }
        return methods.add(testCase);
    }
    
    private static Collection<? extends TestUnit> findJUnit3XXTestUnits(final Collection<Class<?>> classes) {
        final List<TestUnit> testUnits = new LinkedList<TestUnit>();
        for (final Class<?> clazz : classes) {
            if (isAbstract(clazz)) {
                continue;
            }
            if (!isJUnit3XXTestSuite(clazz)) {
                continue;
            }
            testUnits.addAll(findJUnit3XXTestUnits(clazz));
        }
        return testUnits;
    }
    
    private static boolean isJUnit3XXTestSuite(Class<?> clazz) {
        do {
            clazz = clazz.getSuperclass();
            if (clazz == TestCase.class) {
                return true;
            }
        } while (clazz != null);
        return false;
    }
    
    private static Collection<? extends TestUnit> findJUnit3XXTestUnits(final Class<?> testSuite) {
        final List<TestUnit> testUnits = new LinkedList<TestUnit>();
        for (final Method method : testSuite.getMethods()) {
            final int mod = method.getModifiers();
            if (!Modifier.isAbstract(mod) && !Modifier.isNative(mod)) {
                if (Modifier.isPublic(mod)) {
                    if (method.getReturnType() == Void.TYPE && method.getName().startsWith("test") && shouldAdd(testSuite, method)) {
                        testUnits.add(createTestUnit(testSuite, method));
                    }
                }
            }
        }
        return testUnits;
    }
    
    private static Collection<? extends TestUnit> findJUnit4YYTestUnits(final Collection<Class<?>> classes) {
        final List<TestUnit> testUnits = new LinkedList<TestUnit>();
        for (final Class<?> clazz : classes) {
            if (isAbstract(clazz)) {
                continue;
            }
            for (final Method method : clazz.getMethods()) {
                final int mod = method.getModifiers();
                if (!Modifier.isAbstract(mod) && !Modifier.isNative(mod)) {
                    if (Modifier.isPublic(mod)) {
                        final Test annotation = method.getAnnotation(Test.class);
                        if (annotation != null && shouldAdd(clazz, method)) {
                            testUnits.add(createTestUnit(clazz, method));
                        }
                    }
                }
            }
        }
        return testUnits;
    }
    
    private static TestUnit createTestUnit(final Class<?> testSuite, final Method testCase) {
        final Description testDescription = Description.createTestDescription(testSuite, testCase.getName(), testCase.getDeclaredAnnotations());
        final Filter filter = Filter.matchMethodDescription(testDescription);
        return new AdaptedJUnitTestUnit(testSuite, Option.some(filter));
    }
    
    private static boolean isAbstract(final Class<?> clazz) {
        final int mod = clazz.getModifiers();
        return Modifier.isInterface(mod) || Modifier.isAbstract(mod);
    }
    
    static {
        VISITED = new HashMap<Class<?>, Set<Method>>();
    }
}
