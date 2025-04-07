// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator.converters.basic;

import java.net.MalformedURLException;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import java.net.URL;

public class UrlConverter extends AbstractBasicConverter
{
    public boolean canConvert(final Class type) {
        return type.equals(URL.class);
    }
    
    public Object fromString(final String str) throws ComponentConfigurationException {
        try {
            return new URL(str);
        }
        catch (MalformedURLException e) {
            throw new ComponentConfigurationException("Unable to convert '" + str + "' to an URL", e);
        }
    }
}
