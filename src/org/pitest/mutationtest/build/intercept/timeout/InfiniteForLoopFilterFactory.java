// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build.intercept.timeout;

import org.pitest.plugin.Feature;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.mutationtest.build.MutationInterceptorFactory;

public class InfiniteForLoopFilterFactory implements MutationInterceptorFactory
{
    @Override
    public String description() {
        return "Infinite for loop filter";
    }
    
    @Override
    public MutationInterceptor createInterceptor(final InterceptorParameters params) {
        return new InfiniteForLoopFilter();
    }
    
    @Override
    public Feature provides() {
        return Feature.named("FINFINC").withOnByDefault(true).withDescription("Filters mutations to increments that may cause infinite loops");
    }
}
