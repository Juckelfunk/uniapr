// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build.intercept.logging;

import org.pitest.functional.F;
import org.pitest.functional.prelude.Prelude;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;
import org.objectweb.asm.MethodVisitor;
import java.util.Iterator;
import org.pitest.bytecode.analysis.MethodTree;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.mutationtest.build.InterceptorType;
import org.pitest.functional.FCollection;
import java.util.HashSet;
import java.util.Collection;
import java.util.Set;
import org.pitest.mutationtest.build.MutationInterceptor;

public class LoggingCallsFilter implements MutationInterceptor
{
    private final Set<String> loggingClasses;
    private Set<Integer> lines;
    
    public LoggingCallsFilter(final Collection<String> loggingClasses) {
        this.loggingClasses = new HashSet<String>();
        FCollection.mapTo(loggingClasses, correctFormat(), this.loggingClasses);
    }
    
    @Override
    public InterceptorType type() {
        return InterceptorType.FILTER;
    }
    
    @Override
    public void begin(final ClassTree clazz) {
        this.lines = new HashSet<Integer>();
        for (final MethodTree each : clazz.methods()) {
            this.findLoggingLines(each, this.lines);
        }
    }
    
    private void findLoggingLines(final MethodTree each, final Set<Integer> lines) {
        each.rawNode().accept((MethodVisitor)new LoggingLineScanner(lines, this.loggingClasses));
    }
    
    @Override
    public Collection<MutationDetails> intercept(final Collection<MutationDetails> mutations, final Mutater m) {
        return (Collection<MutationDetails>)FCollection.filter(mutations, (F<Object, Boolean>)Prelude.not(this.isOnLoggingLine()));
    }
    
    private F<MutationDetails, Boolean> isOnLoggingLine() {
        return new F<MutationDetails, Boolean>() {
            @Override
            public Boolean apply(final MutationDetails a) {
                return LoggingCallsFilter.this.lines.contains(a.getClassLine().getLineNumber());
            }
        };
    }
    
    @Override
    public void end() {
        this.lines = null;
    }
    
    private static F<String, String> correctFormat() {
        return new F<String, String>() {
            @Override
            public String apply(final String a) {
                return a.replace('.', '/');
            }
        };
    }
}
