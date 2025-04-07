// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build;

import org.pitest.util.Log;
import org.pitest.mutationtest.MutationMetaData;
import org.pitest.mutationtest.MutationResult;
import java.util.List;
import java.util.logging.Logger;

public class KnownStatusMutationTestUnit implements MutationAnalysisUnit
{
    private static final Logger LOG;
    private final List<MutationResult> mutations;
    
    public KnownStatusMutationTestUnit(final List<MutationResult> mutations) {
        this.mutations = mutations;
    }
    
    @Override
    public MutationMetaData call() throws Exception {
        KnownStatusMutationTestUnit.LOG.fine("Using historic results for " + this.mutations.size() + " mutations");
        return new MutationMetaData(this.mutations);
    }
    
    @Override
    public int priority() {
        return Integer.MAX_VALUE;
    }
    
    static {
        LOG = Log.getLogger();
    }
}
