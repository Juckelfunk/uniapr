// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator.converters.basic;

public class CharConverter extends AbstractBasicConverter
{
    public boolean canConvert(final Class type) {
        return type.equals(Character.TYPE) || type.equals(Character.class);
    }
    
    public Object fromString(final String str) {
        return new Character(str.charAt(0));
    }
}
