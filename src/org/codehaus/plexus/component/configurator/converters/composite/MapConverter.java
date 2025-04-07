// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator.converters.composite;

import java.util.Properties;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import java.util.Map;
import java.util.TreeMap;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.converters.AbstractConfigurationConverter;

public class MapConverter extends AbstractConfigurationConverter
{
    public boolean canConvert(final Class type) {
        return Map.class.isAssignableFrom(type) && !Properties.class.isAssignableFrom(type);
    }
    
    public Object fromConfiguration(final ConverterLookup converterLookup, final PlexusConfiguration configuration, final Class type, final Class baseType, final ClassLoader classLoader, final ExpressionEvaluator expressionEvaluator, final ConfigurationListener listener) throws ComponentConfigurationException {
        final String expression = configuration.getValue(null);
        Object retValue;
        if (expression == null) {
            final Map map = new TreeMap();
            final PlexusConfiguration[] children = configuration.getChildren();
            for (int i = 0; i < children.length; ++i) {
                final PlexusConfiguration child = children[i];
                final String name = child.getName();
                map.put(name, this.fromExpression(child, expressionEvaluator));
            }
            retValue = map;
        }
        else {
            retValue = this.fromExpression(configuration, expressionEvaluator);
        }
        return retValue;
    }
}
