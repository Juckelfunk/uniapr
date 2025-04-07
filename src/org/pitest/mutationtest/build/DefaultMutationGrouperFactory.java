// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build;

import org.pitest.classpath.CodeSource;
import java.util.Properties;

public class DefaultMutationGrouperFactory implements MutationGrouperFactory
{
    @Override
    public String description() {
        return "Default mutation grouping";
    }
    
    @Override
    public MutationGrouper makeFactory(final Properties props, final CodeSource codeSource, final int numberOfThreads, final int unitSize) {
        return new DefaultGrouper(unitSize);
    }
}
