// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest;

import java.util.Properties;
import org.pitest.plugin.ToolClasspathPlugin;

public interface MutationResultListenerFactory extends ToolClasspathPlugin
{
    MutationResultListener getListener(final Properties p0, final ListenerArguments p1);
    
    String name();
}
