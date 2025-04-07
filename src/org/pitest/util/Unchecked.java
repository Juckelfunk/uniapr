// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.util;

public abstract class Unchecked
{
    public static RuntimeException translateCheckedException(final Throwable ex) {
        return new PitError(ex.getMessage(), ex);
    }
    
    public static RuntimeException translateCheckedException(final String message, final Throwable ex) {
        return new PitError(message, ex);
    }
}
