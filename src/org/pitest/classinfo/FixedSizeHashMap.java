// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.classinfo;

import java.util.Map;
import java.util.LinkedHashMap;

class FixedSizeHashMap<K, V> extends LinkedHashMap<K, V>
{
    private final int maxsize;
    private static final long serialVersionUID = 2648931151905594122L;
    
    FixedSizeHashMap(final int maxsize) {
        this.maxsize = maxsize;
    }
    
    @Override
    protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
        return this.size() > this.maxsize;
    }
}
