// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr;

import edu.utdallas.prf.reloc.apache.commons.io.FileUtils;
import org.uniapr.commons.misc.MemberNameUtils;
import java.io.File;
import edu.utdallas.prf.reloc.apache.commons.lang3.Validate;
import java.io.Serializable;

public class LoadedClass implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final String classJavaName;
    private final byte[] bytes;
    
    public LoadedClass(final String className, final byte[] bytes) {
        Validate.notNull(className);
        this.classJavaName = className.replace('/', '.');
        this.bytes = bytes;
    }
    
    public static LoadedClass fromFile(final File classFile) throws Exception {
        final String className = MemberNameUtils.getClassName(classFile);
        final byte[] bytes = FileUtils.readFileToByteArray(classFile);
        return new LoadedClass(className, bytes);
    }
    
    public String getJavaName() {
        return this.classJavaName;
    }
    
    public String getInternalName() {
        return this.classJavaName.replace('.', '/');
    }
    
    public byte[] getBytes() {
        return this.bytes;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final LoadedClass that = (LoadedClass)o;
        return this.classJavaName.equals(that.classJavaName);
    }
    
    @Override
    public int hashCode() {
        return this.classJavaName.hashCode();
    }
}
