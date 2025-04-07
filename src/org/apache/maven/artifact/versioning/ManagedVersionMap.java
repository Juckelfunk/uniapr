// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.versioning;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

public class ManagedVersionMap extends HashMap
{
    public ManagedVersionMap(final Map map) {
        if (map != null) {
            this.putAll(map);
        }
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer("ManagedVersionMap\n");
        final Iterator iter = this.keySet().iterator();
        while (iter.hasNext()) {
            final String key = iter.next();
            buffer.append(key).append("=").append(this.get(key));
            if (iter.hasNext()) {
                buffer.append("\n");
            }
        }
        return buffer.toString();
    }
}
