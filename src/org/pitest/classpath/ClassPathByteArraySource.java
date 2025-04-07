// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.classpath;

import org.pitest.util.Log;
import java.io.IOException;
import org.pitest.functional.Option;
import java.util.logging.Logger;
import org.pitest.classinfo.ClassByteArraySource;

public class ClassPathByteArraySource implements ClassByteArraySource
{
    private static final Logger LOG;
    private final ClassPath classPath;
    
    public ClassPathByteArraySource() {
        this(new ClassPath());
    }
    
    public ClassPathByteArraySource(final ClassPath classPath) {
        this.classPath = classPath;
    }
    
    @Override
    public Option<byte[]> getBytes(final String classname) {
        try {
            return Option.some(this.classPath.getClassData(classname));
        }
        catch (IOException e) {
            ClassPathByteArraySource.LOG.fine("Could not read class " + classname + ":" + e.getMessage());
            return (Option<byte[]>)Option.none();
        }
    }
    
    static {
        LOG = Log.getLogger();
    }
}
