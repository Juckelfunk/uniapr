// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr.profiler;

import org.pitest.functional.predicate.Predicate;
import java.util.Map;

public interface ProfilerResults
{
    Map<String, Long> getTestsTiming();
    
    Predicate<String> getIsFailingTestPredicate();
}
