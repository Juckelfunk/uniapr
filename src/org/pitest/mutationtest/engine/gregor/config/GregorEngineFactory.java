// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.config;

import org.pitest.util.Glob;
import org.pitest.functional.F;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.functional.predicate.Predicate;
import org.pitest.mutationtest.engine.gregor.MutationEngineConfiguration;
import org.pitest.mutationtest.engine.gregor.GregorMutationEngine;
import org.pitest.functional.prelude.Prelude;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import java.util.Collection;
import org.pitest.mutationtest.engine.MutationEngine;
import org.pitest.mutationtest.EngineArguments;
import org.pitest.mutationtest.MutationEngineFactory;

public final class GregorEngineFactory implements MutationEngineFactory
{
    @Override
    public MutationEngine createEngine(final EngineArguments args) {
        return this.createEngineWithMutators(args.excludedMethods(), createMutatorListFromArrayOrUseDefaults(args.mutators()));
    }
    
    public MutationEngine createEngineWithMutators(final Collection<String> excludedMethods, final Collection<? extends MethodMutatorFactory> mutators) {
        final Predicate<MethodInfo> filter = Prelude.not(stringToMethodInfoPredicate(excludedMethods));
        final DefaultMutationEngineConfiguration config = new DefaultMutationEngineConfiguration(filter, mutators);
        return new GregorMutationEngine(config);
    }
    
    private static Collection<? extends MethodMutatorFactory> createMutatorListFromArrayOrUseDefaults(final Collection<String> mutators) {
        if (mutators != null && !mutators.isEmpty()) {
            return Mutator.fromStrings(mutators);
        }
        return Mutator.defaults();
    }
    
    private static F<MethodInfo, Boolean> stringToMethodInfoPredicate(final Collection<String> excludedMethods) {
        final Predicate<String> excluded = Prelude.or(Glob.toGlobPredicates(excludedMethods));
        return new Predicate<MethodInfo>() {
            @Override
            public Boolean apply(final MethodInfo a) {
                return (Boolean)excluded.apply(a.getName());
            }
        };
    }
    
    @Override
    public String name() {
        return "gregor";
    }
    
    @Override
    public String description() {
        return "Default mutation engine";
    }
}
