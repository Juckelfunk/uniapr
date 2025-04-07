// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.junit;

import java.util.Iterator;
import java.util.Collections;
import org.pitest.testapi.TestUnit;
import java.util.List;
import org.pitest.testapi.TestUnitFinder;

public class CompoundTestUnitFinder implements TestUnitFinder
{
    private final List<TestUnitFinder> tufs;
    
    public CompoundTestUnitFinder(final List<TestUnitFinder> tufs) {
        this.tufs = tufs;
    }
    
    @Override
    public List<TestUnit> findTestUnits(final Class<?> clazz) {
        for (final TestUnitFinder each : this.tufs) {
            final List<TestUnit> tus = each.findTestUnits(clazz);
            if (!tus.isEmpty()) {
                return tus;
            }
        }
        return Collections.emptyList();
    }
}
