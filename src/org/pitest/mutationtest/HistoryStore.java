// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest;

import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.engine.MutationIdentifier;
import java.util.Map;
import org.pitest.coverage.CoverageDatabase;
import org.pitest.classinfo.HierarchicalClassId;
import java.util.Collection;

public interface HistoryStore
{
    void initialize();
    
    void recordClassPath(final Collection<HierarchicalClassId> p0, final CoverageDatabase p1);
    
    void recordResult(final MutationResult p0);
    
    Map<MutationIdentifier, MutationStatusTestPair> getHistoricResults();
    
    Map<ClassName, ClassHistory> getHistoricClassPath();
}
