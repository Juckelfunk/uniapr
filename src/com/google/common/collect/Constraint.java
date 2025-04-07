// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.collect;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
interface Constraint<E>
{
    @CanIgnoreReturnValue
    E checkElement(final E p0);
    
    String toString();
}
