// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build.intercept.annotations;

import java.util.Iterator;
import org.pitest.functional.FCollection;
import java.util.Collections;
import org.pitest.mutationtest.engine.Mutater;
import java.util.Collection;
import org.objectweb.asm.tree.AnnotationNode;
import org.pitest.bytecode.analysis.MethodTree;
import org.pitest.functional.F;
import org.pitest.functional.FunctionalList;
import org.pitest.functional.prelude.Prelude;
import org.pitest.bytecode.analysis.AnalysisFunctions;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.mutationtest.build.InterceptorType;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.functional.predicate.Predicate;
import java.util.List;
import org.pitest.mutationtest.build.MutationInterceptor;

public class ExcludedAnnotationInterceptor implements MutationInterceptor
{
    private final List<String> configuredAnnotations;
    private boolean skipClass;
    private Predicate<MutationDetails> annotatedMethodMatcher;
    
    ExcludedAnnotationInterceptor(final List<String> configuredAnnotations) {
        this.configuredAnnotations = configuredAnnotations;
    }
    
    @Override
    public InterceptorType type() {
        return InterceptorType.FILTER;
    }
    
    @Override
    public void begin(final ClassTree clazz) {
        if (!(this.skipClass = clazz.annotations().contains(this.avoidedAnnotation()))) {
            final FunctionalList<Predicate<MutationDetails>> methods = clazz.methods().filter(this.hasAvoidedAnnotation()).map(AnalysisFunctions.matchMutationsInMethod());
            this.annotatedMethodMatcher = Prelude.or(methods);
        }
    }
    
    private F<MethodTree, Boolean> hasAvoidedAnnotation() {
        return new F<MethodTree, Boolean>() {
            @Override
            public Boolean apply(final MethodTree a) {
                return a.annotations().contains(ExcludedAnnotationInterceptor.this.avoidedAnnotation());
            }
        };
    }
    
    private F<AnnotationNode, Boolean> avoidedAnnotation() {
        return new F<AnnotationNode, Boolean>() {
            @Override
            public Boolean apply(final AnnotationNode a) {
                return ExcludedAnnotationInterceptor.this.shouldAvoid(a.desc);
            }
        };
    }
    
    @Override
    public Collection<MutationDetails> intercept(final Collection<MutationDetails> mutations, final Mutater m) {
        if (this.skipClass) {
            return (Collection<MutationDetails>)Collections.emptyList();
        }
        return (Collection<MutationDetails>)FCollection.filter(mutations, (F<Object, Boolean>)Prelude.not((F<Object, Boolean>)this.annotatedMethodMatcher));
    }
    
    @Override
    public void end() {
    }
    
    boolean shouldAvoid(final String desc) {
        final String matchAgainst = desc.replace(";", "");
        for (final String each : this.configuredAnnotations) {
            if (matchAgainst.endsWith(each)) {
                return true;
            }
        }
        return false;
    }
}
