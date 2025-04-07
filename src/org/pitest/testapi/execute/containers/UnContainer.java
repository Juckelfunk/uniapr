// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.testapi.execute.containers;

import org.pitest.testapi.ResultCollector;
import java.util.Collection;
import java.util.ArrayList;
import org.pitest.testapi.TestResult;
import java.util.List;
import org.pitest.testapi.TestUnit;
import org.pitest.testapi.execute.Container;

public class UnContainer implements Container
{
    @Override
    public List<TestResult> execute(final TestUnit group) {
        final List<TestResult> results = new ArrayList<TestResult>(12);
        final ConcreteResultCollector rc = new ConcreteResultCollector(results);
        group.execute(rc);
        return results;
    }
}
