// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.testapi.execute;

import org.pitest.functional.F;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import org.pitest.testapi.TestUnit;
import java.util.List;
import org.pitest.testapi.Configuration;

public class FindTestUnits
{
    private final Configuration config;
    
    public FindTestUnits(final Configuration config) {
        this.config = config;
    }
    
    public List<TestUnit> findTestUnitsForAllSuppliedClasses(final Iterable<Class<?>> classes) {
        final List<TestUnit> testUnits = new ArrayList<TestUnit>();
        for (final Class<?> c : classes) {
            final Collection<TestUnit> testUnitsFromClass = this.getTestUnits(c);
            testUnits.addAll(testUnitsFromClass);
        }
        return testUnits;
    }
    
    private Collection<TestUnit> getTestUnits(final Class<?> suiteClass) {
        final List<TestUnit> tus = new ArrayList<TestUnit>();
        final Set<Class<?>> visitedClasses = new HashSet<Class<?>>();
        this.findTestUnits(tus, visitedClasses, suiteClass);
        return tus;
    }
    
    private void findTestUnits(final List<TestUnit> tus, final Set<Class<?>> visitedClasses, final Class<?> suiteClass) {
        visitedClasses.add(suiteClass);
        final Collection<Class<?>> tcs = ((F<Class<?>, Collection<Class<?>>>)this.config.testSuiteFinder()).apply(suiteClass);
        for (final Class<?> tc : tcs) {
            if (!visitedClasses.contains(tc)) {
                this.findTestUnits(tus, visitedClasses, tc);
            }
        }
        final List<TestUnit> testsInThisClass = this.config.testUnitFinder().findTestUnits(suiteClass);
        if (!testsInThisClass.isEmpty()) {
            tus.addAll(testsInThisClass);
        }
    }
}
