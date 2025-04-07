// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator.converters.basic;

import org.codehaus.plexus.component.configurator.ComponentConfigurationException;

public class IntConverter extends AbstractBasicConverter
{
    public boolean canConvert(final Class type) {
        return type.equals(Integer.TYPE) || type.equals(Integer.class);
    }
    
    public Object fromString(final String str) throws ComponentConfigurationException {
        try {
            return Integer.valueOf(str);
        }
        catch (NumberFormatException e) {
            throw new ComponentConfigurationException("Not a number: '" + str + "'", e);
        }
    }
}
