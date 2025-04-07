// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.execute;

import org.pitest.mutationtest.config.TestPluginArguments;
import org.pitest.mutationtest.TimeoutLengthStrategy;
import org.pitest.mutationtest.EngineArguments;
import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.engine.MutationDetails;
import java.util.Collection;
import java.io.Serializable;

public class MinionArguments implements Serializable
{
    private static final long serialVersionUID = 1L;
    final Collection<MutationDetails> mutations;
    final Collection<ClassName> testClasses;
    final String engine;
    final EngineArguments engineArgs;
    final TimeoutLengthStrategy timeoutStrategy;
    final boolean verbose;
    final TestPluginArguments pitConfig;
    
    public MinionArguments(final Collection<MutationDetails> mutations, final Collection<ClassName> tests, final String engine, final EngineArguments engineArgs, final TimeoutLengthStrategy timeoutStrategy, final boolean verbose, final TestPluginArguments pitConfig) {
        this.mutations = mutations;
        this.testClasses = tests;
        this.engine = engine;
        this.engineArgs = engineArgs;
        this.timeoutStrategy = timeoutStrategy;
        this.verbose = verbose;
        this.pitConfig = pitConfig;
    }
    
    public boolean isVerbose() {
        return this.verbose;
    }
}
