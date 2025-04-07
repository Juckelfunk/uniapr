// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator.converters.basic;

public class ByteConverter extends AbstractBasicConverter
{
    public boolean canConvert(final Class type) {
        return type.equals(Byte.TYPE) || type.equals(Byte.class);
    }
    
    public Object fromString(final String str) {
        return new Byte((byte)Integer.parseInt(str));
    }
}
