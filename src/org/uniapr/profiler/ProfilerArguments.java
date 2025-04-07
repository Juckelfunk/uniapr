// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr.profiler;

import java.util.Collection;
import java.util.List;
import org.uniapr.commons.process.ChildProcessCommonArguments;

class ProfilerArguments extends ChildProcessCommonArguments
{
    public ProfilerArguments(final List<String> appClassNames) {
        super(appClassNames);
    }
}
