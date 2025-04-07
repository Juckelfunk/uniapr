// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.testapi;

import java.util.Collection;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.plugin.ClientClasspathPlugin;

public interface TestPluginFactory extends ClientClasspathPlugin
{
    Configuration createTestFrameworkConfiguration(final TestGroupConfig p0, final ClassByteArraySource p1, final Collection<String> p2, final Collection<String> p3);
    
    String name();
}
