// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build.intercept.kotlin;

import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.mutationtest.build.InterceptorType;
import org.pitest.functional.F;
import org.pitest.functional.FCollection;
import org.pitest.functional.prelude.Prelude;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;
import java.util.Collection;
import org.pitest.mutationtest.build.MutationInterceptor;

public class KotlinFilter implements MutationInterceptor
{
    @Override
    public Collection<MutationDetails> intercept(final Collection<MutationDetails> mutations, final Mutater m) {
        return (Collection<MutationDetails>)FCollection.filter(mutations, (F<Object, Boolean>)Prelude.not(isKotlinJunkMutation()));
    }
    
    @Override
    public InterceptorType type() {
        return InterceptorType.FILTER;
    }
    
    @Override
    public void begin(final ClassTree clazz) {
    }
    
    @Override
    public void end() {
    }
    
    private static F<MutationDetails, Boolean> isKotlinJunkMutation() {
        return new F<MutationDetails, Boolean>() {
            @Override
            public Boolean apply(final MutationDetails a) {
                return a.getFilename().toLowerCase().endsWith(".kt") && a.getLineNumber() == 0;
            }
        };
    }
}
