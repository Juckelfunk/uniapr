// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator.converters.composite;

import java.util.Hashtable;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import java.util.Properties;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.converters.AbstractConfigurationConverter;

public class PropertiesConverter extends AbstractConfigurationConverter
{
    public boolean canConvert(final Class type) {
        return Properties.class.isAssignableFrom(type);
    }
    
    public Object fromConfiguration(final ConverterLookup converterLookup, final PlexusConfiguration configuration, final Class type, final Class baseType, final ClassLoader classLoader, final ExpressionEvaluator expressionEvaluator, final ConfigurationListener listener) throws ComponentConfigurationException {
        final Object retValueInterpolated = this.fromExpression(configuration, expressionEvaluator, type);
        if (retValueInterpolated != null) {
            return retValueInterpolated;
        }
        final String element = configuration.getName();
        final Properties retValue = new Properties();
        final PlexusConfiguration[] children = configuration.getChildren("property");
        if (children != null && children.length > 0) {
            for (int i = 0; i < children.length; ++i) {
                final PlexusConfiguration child = children[i];
                this.addEntry(retValue, element, child);
            }
        }
        return retValue;
    }
    
    private void addEntry(final Properties properties, final String element, final PlexusConfiguration property) throws ComponentConfigurationException {
        final String name = property.getChild("name").getValue(null);
        if (name == null) {
            final String msg = "Converter: java.util.Properties. Trying to convert the configuration element: '" + element + "', missing child element 'name'.";
            throw new ComponentConfigurationException(msg);
        }
        final String value = property.getChild("value").getValue("");
        ((Hashtable<String, String>)properties).put(name, value);
    }
}
