// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build;

import org.pitest.functional.F;
import org.pitest.functional.FCollection;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.mutationtest.config.ReportOptions;
import java.util.Collection;
import org.pitest.plugin.FeatureSetting;
import java.util.List;
import org.pitest.plugin.FeatureSelector;

public class CompoundInterceptorFactory
{
    private final FeatureSelector<MutationInterceptorFactory> features;
    
    public CompoundInterceptorFactory(final List<FeatureSetting> features, final Collection<MutationInterceptorFactory> filters) {
        this.features = new FeatureSelector<MutationInterceptorFactory>(features, filters);
    }
    
    public MutationInterceptor createInterceptor(final ReportOptions data, final ClassByteArraySource source) {
        final List<MutationInterceptor> interceptors = FCollection.map(this.features.getActiveFeatures(), toInterceptor(this.features, data, source));
        return new CompoundMutationInterceptor(interceptors);
    }
    
    private static F<MutationInterceptorFactory, MutationInterceptor> toInterceptor(final FeatureSelector<MutationInterceptorFactory> features, final ReportOptions data, final ClassByteArraySource source) {
        return new F<MutationInterceptorFactory, MutationInterceptor>() {
            @Override
            public MutationInterceptor apply(final MutationInterceptorFactory a) {
                return a.createInterceptor(new InterceptorParameters(features.getSettingForFeature(a.provides().name()), data, source));
            }
        };
    }
}
