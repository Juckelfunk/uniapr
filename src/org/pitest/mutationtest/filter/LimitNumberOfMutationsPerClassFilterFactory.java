// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.filter;

import org.pitest.functional.Option;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.plugin.Feature;
import org.pitest.plugin.FeatureParameter;
import org.pitest.mutationtest.build.MutationInterceptorFactory;

public class LimitNumberOfMutationsPerClassFilterFactory implements MutationInterceptorFactory
{
    private final FeatureParameter limit;
    
    public LimitNumberOfMutationsPerClassFilterFactory() {
        this.limit = FeatureParameter.named("limit").withDescription("Integer value for maximum mutations to create per class");
    }
    
    @Override
    public String description() {
        return "Max mutations per class limit";
    }
    
    @Override
    public Feature provides() {
        return Feature.named("CLASSLIMIT").withDescription("Limits the maximum number of mutations per class").withParameter(this.limit);
    }
    
    @Override
    public MutationInterceptor createInterceptor(final InterceptorParameters params) {
        final Option<Integer> max = params.getInteger(this.limit);
        if (max.hasNone()) {
            throw new IllegalArgumentException("Max mutation per class filter requires a limit parameter");
        }
        return new LimitNumberOfMutationPerClassFilter(max.value());
    }
}
