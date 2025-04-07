// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr.junit.runner;

import org.uniapr.commons.misc.MemberNameUtils;
import java.util.Collection;
import org.pitest.testapi.TestUnit;
import org.pitest.functional.predicate.Predicate;

public class TestUnitFilter
{
    public static Predicate<TestUnit> all() {
        return new Predicate<TestUnit>() {
            @Override
            public Boolean apply(final TestUnit testUnit) {
                return Boolean.TRUE;
            }
        };
    }
    
    public static Predicate<TestUnit> some(final Collection<String> testUnitNames) {
        return new Predicate<TestUnit>() {
            @Override
            public Boolean apply(final TestUnit testUnit) {
                final String testName = MemberNameUtils.sanitizeExtendedTestName(testUnit.getDescription().getName());
                return testUnitNames.contains(testName);
            }
        };
    }
}
