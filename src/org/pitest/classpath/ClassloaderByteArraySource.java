// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.classpath;

import java.io.IOException;
import org.pitest.util.Unchecked;
import org.pitest.functional.Option;
import org.pitest.util.IsolationUtils;
import org.pitest.classinfo.ClassByteArraySource;

public class ClassloaderByteArraySource implements ClassByteArraySource
{
    private final ClassPath cp;
    
    public ClassloaderByteArraySource(final ClassLoader loader) {
        this.cp = new ClassPath(new ClassPathRoot[] { new OtherClassLoaderClassPathRoot(loader) });
    }
    
    public static ClassloaderByteArraySource fromContext() {
        return new ClassloaderByteArraySource(IsolationUtils.getContextClassLoader());
    }
    
    @Override
    public Option<byte[]> getBytes(final String classname) {
        try {
            return Option.some(this.cp.getClassData(classname));
        }
        catch (IOException ex) {
            throw Unchecked.translateCheckedException(ex);
        }
    }
}
