// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build;

import org.pitest.mutationtest.TimeoutLengthStrategy;

public class PercentAndConstantTimeoutStrategy implements TimeoutLengthStrategy
{
    private static final long serialVersionUID = 1L;
    public static final float DEFAULT_FACTOR = 1.25f;
    public static final long DEFAULT_CONSTANT = 4000L;
    private final float percent;
    private final long constant;
    
    public PercentAndConstantTimeoutStrategy(final float percent, final long constant) {
        this.percent = percent;
        this.constant = constant;
    }
    
    @Override
    public long getAllowedTime(final long normalDuration) {
        return Math.round(normalDuration * this.percent) + this.constant;
    }
}
