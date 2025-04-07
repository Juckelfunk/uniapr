// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build;

import java.util.List;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.classinfo.ClassName;
import java.util.Collection;

public interface MutationGrouper
{
    List<List<MutationDetails>> groupMutations(final Collection<ClassName> p0, final Collection<MutationDetails> p1);
}
