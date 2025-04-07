// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine;

import java.util.List;
import org.pitest.classinfo.ClassName;

public interface Mutater
{
    Mutant getMutation(final MutationIdentifier p0);
    
    List<MutationDetails> findMutations(final ClassName p0);
}
