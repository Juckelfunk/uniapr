// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.classinfo;

import org.pitest.functional.Option;
import java.util.Map;

public class CachingByteArraySource implements ClassByteArraySource
{
    private final ClassByteArraySource child;
    private final Map<String, Option<byte[]>> cache;
    
    public CachingByteArraySource(final ClassByteArraySource child, final int maxSize) {
        this.child = child;
        this.cache = new FixedSizeHashMap<String, Option<byte[]>>(maxSize);
    }
    
    @Override
    public Option<byte[]> getBytes(final String clazz) {
        Option<byte[]> maybeBytes = this.cache.get(clazz);
        if (maybeBytes != null) {
            return maybeBytes;
        }
        maybeBytes = this.child.getBytes(clazz);
        this.cache.put(clazz, maybeBytes);
        return maybeBytes;
    }
}
