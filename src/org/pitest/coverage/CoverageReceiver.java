// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.coverage;

import org.pitest.testapi.Description;
import sun.pitest.InvokeReceiver;

public interface CoverageReceiver extends InvokeReceiver
{
    void newTest();
    
    void recordTestOutcome(final Description p0, final boolean p1, final int p2);
}
