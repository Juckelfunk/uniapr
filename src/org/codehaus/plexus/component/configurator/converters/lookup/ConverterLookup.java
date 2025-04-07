// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator.converters.lookup;

import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.converters.ConfigurationConverter;

public interface ConverterLookup
{
    void registerConverter(final ConfigurationConverter p0);
    
    ConfigurationConverter lookupConverterForType(final Class p0) throws ComponentConfigurationException;
}
