// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest;

import org.pitest.mutationtest.engine.Mutater;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.mutationtest.engine.MutationEngine;
import org.pitest.process.LaunchOptions;

public final class MutationConfig
{
    private final LaunchOptions launchOptions;
    private final MutationEngine engine;
    
    public MutationConfig(final MutationEngine engine, final LaunchOptions launchOptions) {
        this.launchOptions = launchOptions;
        this.engine = engine;
    }
    
    public Mutater createMutator(final ClassByteArraySource source) {
        return this.engine.createMutator(source);
    }
    
    public MutationEngine getEngine() {
        return this.engine;
    }
    
    public LaunchOptions getLaunchOptions() {
        return this.launchOptions;
    }
    
    @Override
    public boolean equals(final Object rhs) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String toString() {
        return "MutationConfig [launchOptions=" + this.launchOptions + ", engine=" + this.engine + "]";
    }
}
