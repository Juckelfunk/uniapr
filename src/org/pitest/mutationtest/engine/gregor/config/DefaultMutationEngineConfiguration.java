// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.config;

import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import java.util.Collection;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.functional.predicate.Predicate;
import org.pitest.mutationtest.engine.gregor.MutationEngineConfiguration;

public class DefaultMutationEngineConfiguration implements MutationEngineConfiguration
{
    private final Predicate<MethodInfo> methodFilter;
    private final Collection<? extends MethodMutatorFactory> mutators;
    
    public DefaultMutationEngineConfiguration(final Predicate<MethodInfo> filter, final Collection<? extends MethodMutatorFactory> mutators) {
        this.methodFilter = filter;
        this.mutators = mutators;
    }
    
    @Override
    public Collection<? extends MethodMutatorFactory> mutators() {
        return this.mutators;
    }
    
    @Override
    public Predicate<MethodInfo> methodFilter() {
        return this.methodFilter;
    }
}
