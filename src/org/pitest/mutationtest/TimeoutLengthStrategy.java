// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest;

import java.io.Serializable;

public interface TimeoutLengthStrategy extends Serializable
{
    long getAllowedTime(final long p0);
}
