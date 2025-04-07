// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator.converters.basic;

public class StringBufferConverter extends AbstractBasicConverter
{
    public boolean canConvert(final Class type) {
        return type.equals(StringBuffer.class);
    }
    
    public Object fromString(final String str) {
        return new StringBuffer(str);
    }
}
