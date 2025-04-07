// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator.converters.basic;

public class FloatConverter extends AbstractBasicConverter
{
    public boolean canConvert(final Class type) {
        return type.equals(Float.TYPE) || type.equals(Float.class);
    }
    
    public Object fromString(final String str) {
        return Float.valueOf(str);
    }
}
