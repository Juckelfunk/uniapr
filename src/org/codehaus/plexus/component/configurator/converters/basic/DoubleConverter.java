// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator.converters.basic;

public class DoubleConverter extends AbstractBasicConverter
{
    public boolean canConvert(final Class type) {
        return type.equals(Double.TYPE) || type.equals(Double.class);
    }
    
    public Object fromString(final String str) {
        return Double.valueOf(str);
    }
}
