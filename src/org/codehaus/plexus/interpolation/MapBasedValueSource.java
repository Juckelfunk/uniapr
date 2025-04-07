// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.interpolation;

import java.util.Map;

public class MapBasedValueSource extends AbstractValueSource
{
    private final Map values;
    
    public MapBasedValueSource(final Map values) {
        super(false);
        this.values = values;
    }
    
    public Object getValue(final String expression) {
        return (this.values == null) ? null : this.values.get(expression);
    }
}
