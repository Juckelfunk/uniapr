// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build.intercept.javafeatures;

import org.pitest.functional.F;
import org.pitest.functional.FCollection;
import org.pitest.functional.prelude.Prelude;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;
import java.util.Collection;
import org.objectweb.asm.MethodVisitor;
import java.util.Iterator;
import org.pitest.bytecode.analysis.MethodTree;
import java.util.HashSet;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.mutationtest.build.InterceptorType;
import java.util.Set;
import org.pitest.mutationtest.build.MutationInterceptor;

public class TryWithResourcesFilter implements MutationInterceptor
{
    private Set<Integer> lines;
    
    @Override
    public InterceptorType type() {
        return InterceptorType.FILTER;
    }
    
    @Override
    public void begin(final ClassTree clazz) {
        this.lines = new HashSet<Integer>();
        for (final MethodTree each : clazz.methods()) {
            this.checkMehod(each, this.lines);
        }
    }
    
    private void checkMehod(final MethodTree each, final Set<Integer> lines) {
        each.rawNode().accept((MethodVisitor)new TryWithResourcesMethodVisitor(lines));
    }
    
    @Override
    public Collection<MutationDetails> intercept(final Collection<MutationDetails> mutations, final Mutater m) {
        return (Collection<MutationDetails>)FCollection.filter(mutations, (F<Object, Boolean>)Prelude.not(this.isOnMarkedLine()));
    }
    
    private F<MutationDetails, Boolean> isOnMarkedLine() {
        return new F<MutationDetails, Boolean>() {
            @Override
            public Boolean apply(final MutationDetails a) {
                return TryWithResourcesFilter.this.lines.contains(a.getClassLine().getLineNumber());
            }
        };
    }
    
    @Override
    public void end() {
    }
}
