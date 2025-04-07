// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor;

import org.pitest.functional.predicate.Predicate;
import java.util.Collection;

public interface MutationEngineConfiguration
{
    Collection<? extends MethodMutatorFactory> mutators();
    
    Predicate<MethodInfo> methodFilter();
}
