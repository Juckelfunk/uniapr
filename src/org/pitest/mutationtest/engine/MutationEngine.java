// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine;

import java.util.Collection;
import org.pitest.classinfo.ClassByteArraySource;

public interface MutationEngine
{
    Mutater createMutator(final ClassByteArraySource p0);
    
    Collection<String> getMutatorNames();
    
    String getName();
}
