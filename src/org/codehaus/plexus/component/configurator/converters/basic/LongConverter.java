// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator.converters.basic;

public class LongConverter extends AbstractBasicConverter
{
    public boolean canConvert(final Class type) {
        return type.equals(Long.TYPE) || type.equals(Long.class);
    }
    
    public Object fromString(final String str) {
        return Long.valueOf(str);
    }
}
