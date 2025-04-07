// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator.converters.lookup;

import org.codehaus.plexus.component.configurator.converters.composite.ObjectWithFieldsConverter;
import org.codehaus.plexus.component.configurator.converters.composite.PlexusConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.composite.PropertiesConverter;
import org.codehaus.plexus.component.configurator.converters.composite.CollectionConverter;
import org.codehaus.plexus.component.configurator.converters.composite.ArrayConverter;
import org.codehaus.plexus.component.configurator.converters.composite.MapConverter;
import org.codehaus.plexus.component.configurator.converters.basic.UrlConverter;
import org.codehaus.plexus.component.configurator.converters.basic.FileConverter;
import org.codehaus.plexus.component.configurator.converters.basic.DateConverter;
import org.codehaus.plexus.component.configurator.converters.basic.StringConverter;
import org.codehaus.plexus.component.configurator.converters.basic.StringBufferConverter;
import org.codehaus.plexus.component.configurator.converters.basic.ShortConverter;
import org.codehaus.plexus.component.configurator.converters.basic.LongConverter;
import org.codehaus.plexus.component.configurator.converters.basic.IntConverter;
import org.codehaus.plexus.component.configurator.converters.basic.FloatConverter;
import org.codehaus.plexus.component.configurator.converters.basic.DoubleConverter;
import org.codehaus.plexus.component.configurator.converters.basic.CharConverter;
import org.codehaus.plexus.component.configurator.converters.basic.ByteConverter;
import org.codehaus.plexus.component.configurator.converters.basic.BooleanConverter;
import java.util.Iterator;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.converters.ConfigurationConverter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;

public class DefaultConverterLookup implements ConverterLookup
{
    private List converters;
    private List customConverters;
    private Map converterMap;
    
    public DefaultConverterLookup() {
        this.converters = new LinkedList();
        this.customConverters = new LinkedList();
        this.converterMap = new HashMap();
        this.registerDefaultBasicConverters();
        this.registerDefaultCompositeConverters();
    }
    
    public void registerConverter(final ConfigurationConverter converter) {
        this.customConverters.add(converter);
    }
    
    protected void registerDefaultConverter(final ConfigurationConverter converter) {
        this.converters.add(converter);
    }
    
    public ConfigurationConverter lookupConverterForType(final Class type) throws ComponentConfigurationException {
        ConfigurationConverter retValue = null;
        if (this.converterMap.containsKey(type)) {
            retValue = this.converterMap.get(type);
        }
        else {
            retValue = this.findConverterForType(this.customConverters, type);
            if (retValue == null) {
                retValue = this.findConverterForType(this.converters, type);
            }
        }
        if (retValue == null) {
            throw new ComponentConfigurationException("Configuration converter lookup failed for type: " + type);
        }
        return retValue;
    }
    
    private ConfigurationConverter findConverterForType(final List converters, final Class type) {
        for (final ConfigurationConverter converter : converters) {
            if (converter.canConvert(type)) {
                this.converterMap.put(type, converter);
                return converter;
            }
        }
        return null;
    }
    
    private void registerDefaultBasicConverters() {
        this.registerDefaultConverter(new BooleanConverter());
        this.registerDefaultConverter(new ByteConverter());
        this.registerDefaultConverter(new CharConverter());
        this.registerDefaultConverter(new DoubleConverter());
        this.registerDefaultConverter(new FloatConverter());
        this.registerDefaultConverter(new IntConverter());
        this.registerDefaultConverter(new LongConverter());
        this.registerDefaultConverter(new ShortConverter());
        this.registerDefaultConverter(new StringBufferConverter());
        this.registerDefaultConverter(new StringConverter());
        this.registerDefaultConverter(new DateConverter());
        this.registerDefaultConverter(new FileConverter());
        this.registerDefaultConverter(new UrlConverter());
    }
    
    private void registerDefaultCompositeConverters() {
        this.registerDefaultConverter(new MapConverter());
        this.registerDefaultConverter(new ArrayConverter());
        this.registerDefaultConverter(new CollectionConverter());
        this.registerDefaultConverter(new PropertiesConverter());
        this.registerDefaultConverter(new PlexusConfigurationConverter());
        this.registerDefaultConverter(new ObjectWithFieldsConverter());
    }
}
