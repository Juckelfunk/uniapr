// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build;

import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;
import java.util.Collection;
import org.pitest.bytecode.analysis.ClassTree;

public interface MutationInterceptor
{
    InterceptorType type();
    
    void begin(final ClassTree p0);
    
    Collection<MutationDetails> intercept(final Collection<MutationDetails> p0, final Mutater p1);
    
    void end();
}
