// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build;

import org.pitest.coverage.CoverageDatabase;
import org.pitest.classpath.CodeSource;
import java.util.Properties;

public class DefaultTestPrioritiserFactory implements TestPrioritiserFactory
{
    @Override
    public String description() {
        return "Default test prioritiser";
    }
    
    @Override
    public TestPrioritiser makeTestPrioritiser(final Properties props, final CodeSource code, final CoverageDatabase coverage) {
        return new DefaultTestPrioritiser(coverage);
    }
}
