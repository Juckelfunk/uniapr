// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build.intercept.staticinitializers;

import org.pitest.plugin.Feature;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.mutationtest.build.MutationInterceptorFactory;

public class StaticInitializerInterceptorFactory implements MutationInterceptorFactory
{
    @Override
    public String description() {
        return "Static initializer code detector plugin";
    }
    
    @Override
    public MutationInterceptor createInterceptor(final InterceptorParameters params) {
        return new StaticInitializerInterceptor();
    }
    
    @Override
    public Feature provides() {
        return Feature.named("FSTATI").withOnByDefault(true).withDescription("Filters mutations in static initializers and code called only from them");
    }
}
