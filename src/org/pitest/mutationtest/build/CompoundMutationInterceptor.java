// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build;

import java.util.Comparator;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;
import java.util.Iterator;
import org.pitest.bytecode.analysis.ClassTree;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class CompoundMutationInterceptor implements MutationInterceptor
{
    private final List<MutationInterceptor> children;
    
    public CompoundMutationInterceptor(final List<? extends MutationInterceptor> interceptors) {
        (this.children = new ArrayList<MutationInterceptor>()).addAll(interceptors);
        Collections.sort(this.children, sortByType());
    }
    
    public static MutationInterceptor nullInterceptor() {
        return new CompoundMutationInterceptor(Collections.emptyList());
    }
    
    @Override
    public void begin(final ClassTree clazz) {
        for (final MutationInterceptor each : this.children) {
            each.begin(clazz);
        }
    }
    
    @Override
    public Collection<MutationDetails> intercept(final Collection<MutationDetails> mutations, final Mutater m) {
        Collection<MutationDetails> modified = mutations;
        for (final MutationInterceptor each : this.children) {
            modified = each.intercept(modified, m);
        }
        return modified;
    }
    
    @Override
    public void end() {
        for (final MutationInterceptor each : this.children) {
            each.end();
        }
    }
    
    @Override
    public InterceptorType type() {
        return InterceptorType.OTHER;
    }
    
    private static Comparator<? super MutationInterceptor> sortByType() {
        return new Comparator<MutationInterceptor>() {
            @Override
            public int compare(final MutationInterceptor o1, final MutationInterceptor o2) {
                return o1.type().compareTo(o2.type());
            }
        };
    }
}
