// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor;

import org.pitest.functional.F;
import org.pitest.functional.FCollection;
import java.util.Collection;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.classinfo.ClassByteArraySource;
import java.util.LinkedHashSet;
import org.pitest.functional.predicate.Predicate;
import java.util.Set;
import org.pitest.mutationtest.engine.MutationEngine;

public class GregorMutationEngine implements MutationEngine
{
    private final Set<MethodMutatorFactory> mutationOperators;
    private final Predicate<MethodInfo> methodFilter;
    
    public GregorMutationEngine(final MutationEngineConfiguration config) {
        this.mutationOperators = new LinkedHashSet<MethodMutatorFactory>();
        this.methodFilter = config.methodFilter();
        this.mutationOperators.addAll(config.mutators());
    }
    
    @Override
    public Mutater createMutator(final ClassByteArraySource byteSource) {
        return new GregorMutater(byteSource, this.methodFilter, this.mutationOperators);
    }
    
    @Override
    public String toString() {
        return "GregorMutationEngine [filter=" + this.methodFilter + ", mutationOperators=" + this.mutationOperators + "]";
    }
    
    @Override
    public Collection<String> getMutatorNames() {
        return FCollection.map(this.mutationOperators, toName());
    }
    
    private static F<MethodMutatorFactory, String> toName() {
        return new F<MethodMutatorFactory, String>() {
            @Override
            public String apply(final MethodMutatorFactory a) {
                return a.getName();
            }
        };
    }
    
    @Override
    public String getName() {
        return "gregor";
    }
}
