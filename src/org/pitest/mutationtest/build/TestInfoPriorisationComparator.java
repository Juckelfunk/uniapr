// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build;

import org.pitest.classinfo.ClassName;
import java.io.Serializable;
import org.pitest.coverage.TestInfo;
import java.util.Comparator;

public class TestInfoPriorisationComparator implements Comparator<TestInfo>, Serializable
{
    private static final long serialVersionUID = 1L;
    private final int distanceTimeWeighting;
    private final ClassName targetClass;
    
    public TestInfoPriorisationComparator(final ClassName targetClass, final int distanceTimeWeighting) {
        this.targetClass = targetClass;
        this.distanceTimeWeighting = distanceTimeWeighting;
    }
    
    @Override
    public int compare(final TestInfo arg0, final TestInfo arg1) {
        final int t0 = arg0.getTime();
        final int t2 = arg1.getTime();
        return t0 - t2 - this.distanceWeighting(arg0, arg1);
    }
    
    private int distanceWeighting(final TestInfo arg0, final TestInfo arg1) {
        return this.weightFor(arg0) - this.weightFor(arg1);
    }
    
    private int weightFor(final TestInfo ti) {
        return this.weightForDirectHit(ti) - ti.getNumberOfBlocksCovered() / 10;
    }
    
    private int weightForDirectHit(final TestInfo arg0) {
        return arg0.directlyHits(this.targetClass) ? this.distanceTimeWeighting : 0;
    }
}
