// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.junit;

import org.pitest.functional.Option;
import org.junit.internal.runners.SuiteMethod;
import java.util.Iterator;
import java.util.Set;
import org.junit.runner.Runner;
import java.util.Collections;
import java.util.Collection;
import org.pitest.functional.F;
import org.pitest.functional.FCollection;
import org.pitest.functional.prelude.Prelude;
import org.pitest.functional.predicate.Predicate;
import java.util.LinkedHashSet;
import org.junit.runner.Description;
import java.util.ArrayList;
import org.pitest.junit.adapter.AdaptedJUnitTestUnit;
import java.util.List;
import org.pitest.testapi.TestSuiteFinder;

public class RunnerSuiteFinder implements TestSuiteFinder
{
    @Override
    public List<Class<?>> apply(final Class<?> a) {
        try {
            final Runner runner = AdaptedJUnitTestUnit.createRunner(a);
            final List<Description> allChildren = new ArrayList<Description>();
            this.flattenChildren(allChildren, runner.getDescription());
            final Set<Class<?>> classes = new LinkedHashSet<Class<?>>(runner.getDescription().getChildren().size());
            final List<Description> suites = (List<Description>)FCollection.filter(allChildren, (F<Object, Boolean>)Prelude.or(isSuiteMethodRunner(runner), isSuite()));
            FCollection.flatMapTo((Iterable<? extends Description>)suites, (F<Description, ? extends Iterable<Object>>)descriptionToTestClass(), (Collection<? super Object>)classes);
            classes.remove(a);
            return new ArrayList<Class<?>>(classes);
        }
        catch (RuntimeException ex) {
            return Collections.emptyList();
        }
    }
    
    private void flattenChildren(final List<Description> allChildren, final Description description) {
        for (final Description each : description.getChildren()) {
            allChildren.add(each);
            this.flattenChildren(allChildren, each);
        }
    }
    
    private static Predicate<Description> isSuiteMethodRunner(final Runner runner) {
        return new Predicate<Description>() {
            @Override
            public Boolean apply(final Description a) {
                return SuiteMethod.class.isAssignableFrom(runner.getClass());
            }
        };
    }
    
    private static F<Description, Option<Class<?>>> descriptionToTestClass() {
        return new F<Description, Option<Class<?>>>() {
            @Override
            public Option<Class<?>> apply(final Description a) {
                final Class<?> clazz = a.getTestClass();
                if (clazz != null) {
                    return Option.some(clazz);
                }
                return (Option<Class<?>>)Option.none();
            }
        };
    }
    
    private static Predicate<Description> isSuite() {
        return new Predicate<Description>() {
            @Override
            public Boolean apply(final Description a) {
                return a.isSuite();
            }
        };
    }
}
