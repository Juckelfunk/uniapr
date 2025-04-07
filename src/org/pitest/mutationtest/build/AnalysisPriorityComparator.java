// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build;

import java.io.Serializable;
import java.util.Comparator;

class AnalysisPriorityComparator implements Comparator<MutationAnalysisUnit>, Serializable
{
    private static final long serialVersionUID = 1L;
    
    @Override
    public int compare(final MutationAnalysisUnit a, final MutationAnalysisUnit b) {
        return b.priority() - a.priority();
    }
}
