// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest;

import org.pitest.mutationtest.engine.MutationEngine;
import org.pitest.plugin.ClientClasspathPlugin;

public interface MutationEngineFactory extends ClientClasspathPlugin
{
    MutationEngine createEngine(final EngineArguments p0);
    
    String name();
}
