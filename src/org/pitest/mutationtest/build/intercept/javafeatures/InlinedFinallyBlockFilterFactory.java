// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build.intercept.javafeatures;

import org.pitest.plugin.Feature;
import org.pitest.mutationtest.build.CompoundMutationInterceptor;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.mutationtest.build.MutationInterceptorFactory;

public class InlinedFinallyBlockFilterFactory implements MutationInterceptorFactory
{
    @Override
    public String description() {
        return "Inlined finally block filter plugin";
    }
    
    @Override
    public MutationInterceptor createInterceptor(final InterceptorParameters params) {
        if (params.data().isDetectInlinedCode()) {
            return new InlinedFinallyBlockFilter();
        }
        return CompoundMutationInterceptor.nullInterceptor();
    }
    
    @Override
    public Feature provides() {
        return Feature.named("FFBLOCK").withOnByDefault(true).withDescription("Filters mutations in code duplicated by finally block inlining");
    }
}
