// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build;

import org.pitest.coverage.CoverageDatabase;
import org.pitest.classpath.CodeSource;
import java.util.Properties;
import org.pitest.plugin.ToolClasspathPlugin;

public interface TestPrioritiserFactory extends ToolClasspathPlugin
{
    TestPrioritiser makeTestPrioritiser(final Properties p0, final CodeSource p1, final CoverageDatabase p2);
}
