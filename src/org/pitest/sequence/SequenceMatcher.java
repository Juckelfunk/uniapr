// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.sequence;

import java.util.List;

public interface SequenceMatcher<T>
{
    boolean matches(final List<T> p0);
    
    boolean matches(final List<T> p0, final Context<T> p1);
}
