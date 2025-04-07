// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest;

import org.pitest.mutationtest.engine.MutationDetails;
import java.util.Collection;

public interface MutationAnalyser
{
    Collection<MutationResult> analyse(final Collection<MutationDetails> p0);
}
