// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.execute;

import org.pitest.util.ExitCode;
import org.pitest.functional.SideEffect;

public class TimeOutSystemExitSideEffect implements SideEffect
{
    private final Reporter r;
    
    public TimeOutSystemExitSideEffect(final Reporter r) {
        this.r = r;
    }
    
    @Override
    public void apply() {
        this.r.done(ExitCode.TIMEOUT);
    }
}
