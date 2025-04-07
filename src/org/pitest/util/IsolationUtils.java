// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.util;

public abstract class IsolationUtils
{
    public static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
    
    public static Class<?> convertForClassLoader(final ClassLoader loader, final String name) {
        try {
            return Class.forName(name, false, loader);
        }
        catch (ClassNotFoundException ex) {
            throw Unchecked.translateCheckedException(ex);
        }
    }
}
