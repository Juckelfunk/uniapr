// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build.intercept.staticinitializers;

import org.pitest.mutationtest.build.InterceptorType;
import org.pitest.functional.F;
import org.pitest.functional.FCollection;
import org.pitest.functional.prelude.Prelude;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;
import java.util.Collection;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.mutationtest.build.MutationInterceptor;

class StaticInitializerFilter implements MutationInterceptor
{
    @Override
    public void begin(final ClassTree clazz) {
    }
    
    @Override
    public Collection<MutationDetails> intercept(final Collection<MutationDetails> mutations, final Mutater m) {
        return (Collection<MutationDetails>)FCollection.filter(mutations, (F<Object, Boolean>)Prelude.not(this.isInStaticInitCode()));
    }
    
    private F<MutationDetails, Boolean> isInStaticInitCode() {
        return new F<MutationDetails, Boolean>() {
            @Override
            public Boolean apply(final MutationDetails a) {
                return a.isInStaticInitializer();
            }
        };
    }
    
    @Override
    public void end() {
    }
    
    @Override
    public InterceptorType type() {
        return InterceptorType.FILTER;
    }
}
