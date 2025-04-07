// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build;

import org.pitest.classpath.CodeSource;
import java.util.Properties;
import org.pitest.plugin.ToolClasspathPlugin;

public interface MutationGrouperFactory extends ToolClasspathPlugin
{
    MutationGrouper makeFactory(final Properties p0, final CodeSource p1, final int p2, final int p3);
}
