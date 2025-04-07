// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build.intercept.annotations;

import org.pitest.plugin.Feature;
import java.util.Arrays;
import java.util.List;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.plugin.FeatureParameter;
import org.pitest.mutationtest.build.MutationInterceptorFactory;

public class ExcludedAnnotationInterceptorFactory implements MutationInterceptorFactory
{
    private static final FeatureParameter ARGUMENT;
    
    @Override
    public String description() {
        return "Excluded annotations plugin";
    }
    
    @Override
    public MutationInterceptor createInterceptor(final InterceptorParameters params) {
        return new ExcludedAnnotationInterceptor(this.determineAnnotations(params));
    }
    
    private List<String> determineAnnotations(final InterceptorParameters params) {
        if (params.getList(ExcludedAnnotationInterceptorFactory.ARGUMENT).isEmpty()) {
            return Arrays.asList("Generated", "DoNotMutate", "CoverageIgnore");
        }
        return params.getList(ExcludedAnnotationInterceptorFactory.ARGUMENT);
    }
    
    @Override
    public Feature provides() {
        return Feature.named("FANN").withOnByDefault(true).withDescription("Filters mutations in classes and methods with matching annotations of class or runtime retention").withParameter(ExcludedAnnotationInterceptorFactory.ARGUMENT);
    }
    
    static {
        ARGUMENT = FeatureParameter.named("annotation").withDescription("Annotation to avoid (full package name not required)");
    }
}
