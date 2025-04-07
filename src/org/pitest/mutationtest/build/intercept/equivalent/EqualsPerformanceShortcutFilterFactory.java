// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build.intercept.equivalent;

import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.plugin.Feature;
import org.pitest.mutationtest.build.MutationInterceptorFactory;

public class EqualsPerformanceShortcutFilterFactory implements MutationInterceptorFactory
{
    @Override
    public String description() {
        return "Equals shortcut equivalent mutant filter";
    }
    
    @Override
    public Feature provides() {
        return Feature.named("FSEQUIVEQUALS").withOnByDefault(true).withDescription("Filters equivalent mutations that affect only performance in short cutting equals methods");
    }
    
    @Override
    public MutationInterceptor createInterceptor(final InterceptorParameters params) {
        return new EqualsPerformanceShortcutFilter();
    }
}
