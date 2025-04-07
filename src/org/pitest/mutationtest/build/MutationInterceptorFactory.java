// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build;

import org.pitest.plugin.ProvidesFeature;
import org.pitest.plugin.ToolClasspathPlugin;

public interface MutationInterceptorFactory extends ToolClasspathPlugin, ProvidesFeature
{
    MutationInterceptor createInterceptor(final InterceptorParameters p0);
}
