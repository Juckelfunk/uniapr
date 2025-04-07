// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build.intercept.equivalent;

import org.pitest.mutationtest.build.InterceptorType;
import java.util.List;
import org.pitest.mutationtest.build.CompoundMutationInterceptor;
import java.util.Arrays;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.plugin.Feature;
import org.pitest.mutationtest.build.MutationInterceptorFactory;

public class EquivalentReturnMutationFilter implements MutationInterceptorFactory
{
    @Override
    public String description() {
        return "Trivial return vals equivalence filter";
    }
    
    @Override
    public Feature provides() {
        return Feature.named("FRETEQUIV").withOnByDefault(true).withDescription("Filters return vals mutants with bytecode equivalent to the unmutated class");
    }
    
    @Override
    public MutationInterceptor createInterceptor(final InterceptorParameters params) {
        return new CompoundMutationInterceptor(Arrays.asList(new PrimitiveEquivalentFilter(), new NullReturnsFilter(), new EmptyReturnsFilter(), new HardCodedTrueEquivalentFilter())) {
            @Override
            public InterceptorType type() {
                return InterceptorType.FILTER;
            }
        };
    }
}
