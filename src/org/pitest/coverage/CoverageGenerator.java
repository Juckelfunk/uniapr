// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.coverage;

import org.pitest.process.LaunchOptions;
import org.pitest.mutationtest.config.TestPluginArguments;

public interface CoverageGenerator
{
    CoverageDatabase calculateCoverage();
    
    TestPluginArguments getConfiguration();
    
    LaunchOptions getLaunchOptions();
}
