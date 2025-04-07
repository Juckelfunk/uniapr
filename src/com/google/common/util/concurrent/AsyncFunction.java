// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.util.concurrent;

import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public interface AsyncFunction<I, O>
{
    ListenableFuture<O> apply(@Nullable final I p0) throws Exception;
}
