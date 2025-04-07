// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator.converters.basic;

public class ShortConverter extends AbstractBasicConverter
{
    public boolean canConvert(final Class type) {
        return type.equals(Short.TYPE) || type.equals(Short.class);
    }
    
    public Object fromString(final String str) {
        return Short.valueOf(str);
    }
}
