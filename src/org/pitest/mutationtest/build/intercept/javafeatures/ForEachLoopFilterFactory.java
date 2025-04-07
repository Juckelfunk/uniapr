// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build.intercept.javafeatures;

import org.pitest.plugin.Feature;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.mutationtest.build.MutationInterceptorFactory;

public class ForEachLoopFilterFactory implements MutationInterceptorFactory
{
    @Override
    public String description() {
        return "For each loop filter";
    }
    
    @Override
    public MutationInterceptor createInterceptor(final InterceptorParameters params) {
        return new ForEachLoopFilter();
    }
    
    @Override
    public Feature provides() {
        return Feature.named("FFEACH").withOnByDefault(true).withDescription("Filters mutations in compiler generated code that implements for each loops");
    }
}
