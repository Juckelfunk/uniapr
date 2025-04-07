// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.config;

import org.pitest.functional.F;
import org.pitest.functional.FCollection;
import org.pitest.mutationtest.MutationResultListener;
import org.pitest.mutationtest.ListenerArguments;
import java.util.Properties;
import org.pitest.mutationtest.MutationResultListenerFactory;

public class CompoundListenerFactory implements MutationResultListenerFactory
{
    private final Iterable<MutationResultListenerFactory> children;
    
    public CompoundListenerFactory(final Iterable<MutationResultListenerFactory> children) {
        this.children = children;
    }
    
    @Override
    public MutationResultListener getListener(final Properties props, final ListenerArguments args) {
        return new CompoundTestListener(FCollection.map(this.children, this.factoryToListener(props, args)));
    }
    
    private F<MutationResultListenerFactory, MutationResultListener> factoryToListener(final Properties props, final ListenerArguments args) {
        return new F<MutationResultListenerFactory, MutationResultListener>() {
            @Override
            public MutationResultListener apply(final MutationResultListenerFactory a) {
                return a.getListener(props, args);
            }
        };
    }
    
    @Override
    public String name() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String description() {
        throw new UnsupportedOperationException();
    }
}
