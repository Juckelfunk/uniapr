// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest;

import java.util.Collections;
import java.util.Collection;
import java.io.Serializable;

public class EngineArguments implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final Collection<String> mutators;
    private final Collection<String> excludedMethods;
    
    public EngineArguments(final Collection<String> mutators, final Collection<String> excludedMethods) {
        this.mutators = mutators;
        this.excludedMethods = excludedMethods;
    }
    
    public static EngineArguments arguments() {
        return new EngineArguments((Collection<String>)Collections.emptyList(), (Collection<String>)Collections.emptyList());
    }
    
    public EngineArguments withMutators(final Collection<String> mutators) {
        return new EngineArguments(mutators, this.excludedMethods);
    }
    
    public EngineArguments withExcludedMethods(final Collection<String> excludedMethods) {
        return new EngineArguments(this.mutators, excludedMethods);
    }
    
    public Collection<String> mutators() {
        return this.mutators;
    }
    
    public Collection<String> excludedMethods() {
        return this.excludedMethods;
    }
}
