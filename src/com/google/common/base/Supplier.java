// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.base;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public interface Supplier<T>
{
    @CanIgnoreReturnValue
    T get();
}
