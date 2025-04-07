// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.junit;

import org.junit.runners.Parameterized;
import org.junit.runner.manipulation.Filter;
import org.pitest.functional.Option;
import org.pitest.functional.F;
import java.util.Iterator;
import java.util.Collection;
import org.pitest.functional.FCollection;
import java.util.ArrayList;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import java.util.Collections;
import org.junit.internal.runners.ErrorReportingRunner;
import org.pitest.junit.adapter.AdaptedJUnitTestUnit;
import org.pitest.testapi.TestUnit;
import java.util.List;
import org.pitest.testapi.TestUnitFinder;

public class ParameterisedJUnitTestFinder implements TestUnitFinder
{
    @Override
    public List<TestUnit> findTestUnits(final Class<?> clazz) {
        final Runner runner = AdaptedJUnitTestUnit.createRunner(clazz);
        if (runner == null || runner.getClass().isAssignableFrom(ErrorReportingRunner.class)) {
            return Collections.emptyList();
        }
        if (this.isParameterizedTest(runner)) {
            return this.handleParameterizedTest(clazz, runner.getDescription());
        }
        return Collections.emptyList();
    }
    
    private List<TestUnit> handleParameterizedTest(final Class<?> clazz, final Description description) {
        final List<TestUnit> result = new ArrayList<TestUnit>();
        for (final Description each : description.getChildren()) {
            FCollection.mapTo(each.getChildren(), this.parameterizedToTestUnit(clazz), result);
        }
        return result;
    }
    
    private F<Description, TestUnit> parameterizedToTestUnit(final Class<?> clazz) {
        return new F<Description, TestUnit>() {
            @Override
            public TestUnit apply(final Description a) {
                return new AdaptedJUnitTestUnit(clazz, (Option<Filter>)Option.some(new ParameterisedTestFilter(a.toString())));
            }
        };
    }
    
    private boolean isParameterizedTest(final Runner runner) {
        return Parameterized.class.isAssignableFrom(runner.getClass());
    }
}
