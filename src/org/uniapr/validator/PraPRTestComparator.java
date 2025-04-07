// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr.validator;

import org.uniapr.commons.misc.MemberNameUtils;
import org.pitest.functional.predicate.Predicate;
import java.util.Map;
import java.io.Serializable;
import org.pitest.testapi.TestUnit;
import java.util.Comparator;

public class PraPRTestComparator implements Comparator<TestUnit>, Serializable
{
    private static final long serialVersionUID = 1L;
    private final Map<String, Long> testsTiming;
    private final Predicate<String> isFailing;
    
    public PraPRTestComparator(final Map<String, Long> testsTiming, final Predicate<String> isFailing) {
        this.testsTiming = testsTiming;
        this.isFailing = isFailing;
    }
    
    @Override
    public int compare(final TestUnit t1, final TestUnit t2) {
        final String n1 = MemberNameUtils.sanitizeExtendedTestName(t1.getDescription().getName());
        final String n2 = MemberNameUtils.sanitizeExtendedTestName(t2.getDescription().getName());
        final boolean f1 = this.isFailing.apply(n1);
        final boolean f2 = this.isFailing.apply(n2);
        Long time2 = this.testsTiming.get(n2);
        if (time2 == null) {
            time2 = Long.MAX_VALUE;
        }
        Long time3 = this.testsTiming.get(n1);
        if (time3 == null) {
            time3 = Long.MAX_VALUE;
        }
        return (f1 ^ f2) ? (f1 ? -1 : 1) : Long.compare(time2, time3);
    }
}
