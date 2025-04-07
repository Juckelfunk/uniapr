// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.testapi.execute;

import org.pitest.testapi.TestResult;
import java.util.List;
import org.pitest.testapi.TestUnit;

public interface Container
{
    List<TestResult> execute(final TestUnit p0);
}
