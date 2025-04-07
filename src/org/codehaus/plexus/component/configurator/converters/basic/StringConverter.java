// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator.converters.basic;

public class StringConverter extends AbstractBasicConverter
{
    public boolean canConvert(final Class type) {
        return type.equals(String.class);
    }
    
    public Object fromString(final String str) {
        return str;
    }
}
