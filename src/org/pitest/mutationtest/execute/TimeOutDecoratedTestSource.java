// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.execute;

import org.pitest.functional.SideEffect;
import org.pitest.functional.Option;
import org.pitest.functional.F;
import org.pitest.functional.FCollection;
import org.pitest.coverage.TestInfo;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;
import org.pitest.mutationtest.TimeoutLengthStrategy;
import org.pitest.testapi.TestUnit;
import java.util.Map;

public class TimeOutDecoratedTestSource
{
    private final Map<String, TestUnit> allTests;
    private final TimeoutLengthStrategy timeoutStrategy;
    private final Reporter r;
    
    public TimeOutDecoratedTestSource(final TimeoutLengthStrategy timeoutStrategy, final List<TestUnit> allTests, final Reporter r) {
        this.allTests = new HashMap<String, TestUnit>();
        this.timeoutStrategy = timeoutStrategy;
        this.mapTests(allTests);
        this.r = r;
    }
    
    private void mapTests(final List<TestUnit> tests) {
        for (final TestUnit each : tests) {
            this.allTests.put(each.getDescription().getQualifiedName(), each);
        }
    }
    
    public List<TestUnit> translateTests(final List<TestInfo> testsInOrder) {
        return (List<TestUnit>)FCollection.flatMap((Iterable<? extends TestInfo>)testsInOrder, (F<TestInfo, ? extends Iterable<Object>>)this.testToTestUnit());
    }
    
    private F<TestInfo, Option<TestUnit>> testToTestUnit() {
        return new F<TestInfo, Option<TestUnit>>() {
            @Override
            public Option<TestUnit> apply(final TestInfo a) {
                final TestUnit tu = TimeOutDecoratedTestSource.this.allTests.get(a.getName());
                if (tu != null) {
                    return (Option<TestUnit>)Option.some(new MutationTimeoutDecorator(tu, new TimeOutSystemExitSideEffect(TimeOutDecoratedTestSource.this.r), TimeOutDecoratedTestSource.this.timeoutStrategy, a.getTime()));
                }
                return (Option<TestUnit>)Option.none();
            }
        };
    }
}
