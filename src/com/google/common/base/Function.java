// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.base;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public interface Function<F, T>
{
    @Nullable
    @CanIgnoreReturnValue
    T apply(@Nullable final F p0);
    
    boolean equals(@Nullable final Object p0);
}
