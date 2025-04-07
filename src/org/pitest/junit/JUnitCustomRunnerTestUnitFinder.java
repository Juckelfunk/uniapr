// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.junit;

import org.pitest.util.IsolationUtils;
import org.junit.runners.Parameterized;
import org.pitest.reflection.IsAnnotatedWith;
import java.lang.reflect.Method;
import org.junit.AfterClass;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.util.Set;
import org.junit.BeforeClass;
import org.pitest.reflection.Reflection;
import org.junit.runner.Description;
import org.junit.internal.runners.ErrorReportingRunner;
import org.pitest.functional.F;
import org.pitest.functional.FCollection;
import java.util.Arrays;
import org.junit.experimental.categories.Category;
import java.util.Iterator;
import java.util.ArrayList;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import java.util.Collections;
import org.pitest.junit.adapter.AdaptedJUnitTestUnit;
import org.pitest.testapi.TestUnit;
import java.util.List;
import org.pitest.util.Preconditions;
import java.util.Collection;
import org.pitest.testapi.TestGroupConfig;
import org.pitest.functional.Option;
import org.pitest.testapi.TestUnitFinder;

public class JUnitCustomRunnerTestUnitFinder implements TestUnitFinder
{
    private static final Option<Class> CLASS_RULE;
    private final TestGroupConfig config;
    private final Collection<String> excludedRunners;
    private final Collection<String> includedTestMethods;
    
    JUnitCustomRunnerTestUnitFinder(final TestGroupConfig config, final Collection<String> excludedRunners, final Collection<String> includedTestMethods) {
        Preconditions.checkNotNull(config);
        this.config = config;
        this.excludedRunners = excludedRunners;
        this.includedTestMethods = includedTestMethods;
    }
    
    @Override
    public List<TestUnit> findTestUnits(final Class<?> clazz) {
        final Runner runner = AdaptedJUnitTestUnit.createRunner(clazz);
        if (this.isExcluded(runner) || this.isNotARunnableTest(runner, clazz.getName()) || !this.isIncluded(clazz)) {
            return Collections.emptyList();
        }
        if (Filterable.class.isAssignableFrom(runner.getClass()) && !this.shouldTreatAsOneUnit(clazz, runner)) {
            final List<TestUnit> filteredUnits = this.splitIntoFilteredUnits(runner.getDescription());
            return this.filterUnitsByMethod(filteredUnits);
        }
        return (List<TestUnit>)Collections.singletonList(new AdaptedJUnitTestUnit(clazz, (Option<Filter>)Option.none()));
    }
    
    private List<TestUnit> filterUnitsByMethod(final List<TestUnit> filteredUnits) {
        if (this.includedTestMethods.isEmpty()) {
            return filteredUnits;
        }
        final List<TestUnit> units = new ArrayList<TestUnit>();
        for (final TestUnit unit : filteredUnits) {
            if (this.includedTestMethods.contains(unit.getDescription().getName().split("\\(")[0])) {
                units.add(unit);
            }
        }
        return units;
    }
    
    private boolean isExcluded(final Runner runner) {
        return this.excludedRunners.contains(runner.getClass().getName());
    }
    
    private boolean isIncluded(final Class<?> a) {
        return this.isIncludedCategory(a) && !this.isExcludedCategory(a);
    }
    
    private boolean isIncludedCategory(final Class<?> a) {
        final List<String> included = this.config.getIncludedGroups();
        return included.isEmpty() || !Collections.disjoint(included, this.getCategories(a));
    }
    
    private boolean isExcludedCategory(final Class<?> a) {
        final List<String> excluded = this.config.getExcludedGroups();
        return !excluded.isEmpty() && !Collections.disjoint(excluded, this.getCategories(a));
    }
    
    private List<String> getCategories(final Class<?> a) {
        final Category c = a.getAnnotation(Category.class);
        return (List<String>)FCollection.flatMap((Iterable<? extends Category>)Arrays.asList(c), (F<Category, ? extends Iterable<Object>>)this.toCategoryNames());
    }
    
    private F<Category, Iterable<String>> toCategoryNames() {
        return new F<Category, Iterable<String>>() {
            @Override
            public Iterable<String> apply(final Category a) {
                if (a == null) {
                    return (Iterable<String>)Collections.emptyList();
                }
                return (Iterable<String>)FCollection.map(Arrays.asList(a.value()), (F<Object, Object>)JUnitCustomRunnerTestUnitFinder.this.toName());
            }
        };
    }
    
    private F<Class<?>, String> toName() {
        return new F<Class<?>, String>() {
            @Override
            public String apply(final Class<?> a) {
                return a.getName();
            }
        };
    }
    
    private boolean isNotARunnableTest(final Runner runner, final String className) {
        try {
            return runner == null || runner.getClass().isAssignableFrom(ErrorReportingRunner.class) || this.isParameterizedTest(runner) || this.isAJUnitThreeErrorOrWarning(runner) || this.isJUnitThreeSuiteMethodNotForOwnClass(runner, className);
        }
        catch (RuntimeException ex) {
            return true;
        }
    }
    
    private boolean isAJUnitThreeErrorOrWarning(final Runner runner) {
        return !runner.getDescription().getChildren().isEmpty() && runner.getDescription().getChildren().get(0).getClassName().startsWith("junit.framework.TestSuite");
    }
    
    private boolean shouldTreatAsOneUnit(final Class<?> clazz, final Runner runner) {
        final Set<Method> methods = Reflection.allMethods(clazz);
        return this.runnerCannotBeSplit(runner) || this.hasAnnotation(methods, BeforeClass.class) || this.hasAnnotation(methods, AfterClass.class) || this.hasClassRuleAnnotations(clazz, methods);
    }
    
    private boolean hasClassRuleAnnotations(final Class<?> clazz, final Set<Method> methods) {
        return !JUnitCustomRunnerTestUnitFinder.CLASS_RULE.hasNone() && (this.hasAnnotation(methods, JUnitCustomRunnerTestUnitFinder.CLASS_RULE.value()) || this.hasAnnotation(Reflection.publicFields(clazz), JUnitCustomRunnerTestUnitFinder.CLASS_RULE.value()));
    }
    
    private boolean hasAnnotation(final Set<? extends AccessibleObject> methods, final Class<? extends Annotation> annotation) {
        return FCollection.contains(methods, (F<Object, Boolean>)IsAnnotatedWith.instance(annotation));
    }
    
    private boolean isParameterizedTest(final Runner runner) {
        return Parameterized.class.isAssignableFrom(runner.getClass());
    }
    
    private boolean runnerCannotBeSplit(final Runner runner) {
        final String runnerName = runner.getClass().getName();
        return runnerName.equals("junitparams.JUnitParamsRunner") || runnerName.startsWith("org.spockframework.runtime.Sputnik") || runnerName.startsWith("com.insightfullogic.lambdabehave") || runnerName.startsWith("com.googlecode.yatspec") || runnerName.startsWith("com.google.gwtmockito.GwtMockitoTestRunner");
    }
    
    private boolean isJUnitThreeSuiteMethodNotForOwnClass(final Runner runner, final String className) {
        return runner.getClass().getName().equals("org.junit.internal.runners.SuiteMethod") && !runner.getDescription().getClassName().equals(className);
    }
    
    private List<TestUnit> splitIntoFilteredUnits(final Description description) {
        return FCollection.filter(description.getChildren(), this.isTest()).map(this.descriptionToTestUnit());
    }
    
    private F<Description, TestUnit> descriptionToTestUnit() {
        return new F<Description, TestUnit>() {
            @Override
            public TestUnit apply(final Description a) {
                return JUnitCustomRunnerTestUnitFinder.this.descriptionToTest(a);
            }
        };
    }
    
    private F<Description, Boolean> isTest() {
        return new F<Description, Boolean>() {
            @Override
            public Boolean apply(final Description a) {
                return a.isTest();
            }
        };
    }
    
    private TestUnit descriptionToTest(final Description description) {
        Class<?> clazz = description.getTestClass();
        if (clazz == null) {
            clazz = IsolationUtils.convertForClassLoader(IsolationUtils.getContextClassLoader(), description.getClassName());
        }
        return new AdaptedJUnitTestUnit(clazz, Option.some(this.createFilterFor(description)));
    }
    
    private Filter createFilterFor(final Description description) {
        return new DescriptionFilter(description.toString());
    }
    
    private static Option<Class> findClassRuleClass() {
        try {
            return (Option<Class>)Option.some(Class.forName("org.junit.ClassRule"));
        }
        catch (ClassNotFoundException ex) {
            return (Option<Class>)Option.none();
        }
    }
    
    static {
        CLASS_RULE = findClassRuleClass();
    }
}
