// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build;

import org.pitest.coverage.TestInfo;
import java.util.List;
import org.pitest.mutationtest.engine.MutationDetails;

public interface TestPrioritiser
{
    List<TestInfo> assignTests(final MutationDetails p0);
}
