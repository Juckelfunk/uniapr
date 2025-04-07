// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.context;

import java.util.HashMap;

public class ContextMapAdapter extends HashMap
{
    private Context context;
    
    public ContextMapAdapter(final Context context) {
        this.context = context;
    }
    
    public Object get(final Object key) {
        try {
            final Object value = this.context.get(key);
            if (value instanceof String) {
                return value;
            }
            return null;
        }
        catch (ContextException e) {
            return null;
        }
    }
}
