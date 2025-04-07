// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.execute;

import org.pitest.util.Unchecked;
import org.pitest.boot.HotSwapAgent;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.classinfo.ClassName;
import org.pitest.functional.F3;

class HotSwap implements F3<ClassName, ClassLoader, byte[], Boolean>
{
    private final ClassByteArraySource byteSource;
    private byte[] lastClassPreMutation;
    private ClassName lastMutatedClass;
    private ClassLoader lastUsedLoader;
    
    HotSwap(final ClassByteArraySource byteSource) {
        this.byteSource = byteSource;
    }
    
    @Override
    public Boolean apply(final ClassName clazzName, final ClassLoader loader, final byte[] b) {
        try {
            this.restoreLastClass(this.byteSource, clazzName, loader);
            this.lastUsedLoader = loader;
            final Class<?> clazz = Class.forName(clazzName.asJavaName(), false, loader);
            return HotSwapAgent.hotSwap(clazz, b);
        }
        catch (ClassNotFoundException e) {
            throw Unchecked.translateCheckedException(e);
        }
    }
    
    private void restoreLastClass(final ClassByteArraySource byteSource, final ClassName clazzName, final ClassLoader loader) throws ClassNotFoundException {
        if (this.lastMutatedClass != null && !this.lastMutatedClass.equals(clazzName)) {
            this.restoreForLoader(this.lastUsedLoader);
            this.restoreForLoader(loader);
        }
        if (this.lastMutatedClass == null || !this.lastMutatedClass.equals(clazzName)) {
            this.lastClassPreMutation = byteSource.getBytes(clazzName.asJavaName()).value();
        }
        this.lastMutatedClass = clazzName;
    }
    
    private void restoreForLoader(final ClassLoader loader) throws ClassNotFoundException {
        final Class<?> clazz = Class.forName(this.lastMutatedClass.asJavaName(), false, loader);
        HotSwapAgent.hotSwap(clazz, this.lastClassPreMutation);
    }
}
