// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.filter;

import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.mutationtest.build.InterceptorType;
import java.util.Iterator;
import java.util.ArrayList;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;
import java.util.Collection;
import org.pitest.mutationtest.build.MutationInterceptor;

public class LimitNumberOfMutationPerClassFilter implements MutationInterceptor
{
    private final int maxMutationsPerClass;
    
    public LimitNumberOfMutationPerClassFilter(final int max) {
        this.maxMutationsPerClass = max;
    }
    
    @Override
    public Collection<MutationDetails> intercept(final Collection<MutationDetails> mutations, final Mutater m) {
        if (mutations.size() <= this.maxMutationsPerClass) {
            return mutations;
        }
        return this.createEvenlyDistributedSampling(mutations);
    }
    
    private Collection<MutationDetails> createEvenlyDistributedSampling(final Collection<MutationDetails> mutations) {
        final Collection<MutationDetails> filtered = new ArrayList<MutationDetails>(this.maxMutationsPerClass);
        final int step = mutations.size() / this.maxMutationsPerClass;
        final Iterator<MutationDetails> it = mutations.iterator();
        while (it.hasNext()) {
            int i = 0;
            MutationDetails value = null;
            while (it.hasNext() && i != step) {
                value = it.next();
                ++i;
            }
            if (filtered.size() != this.maxMutationsPerClass) {
                filtered.add(value);
            }
        }
        return filtered;
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
}
