// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator.converters.composite;

import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.converters.ConfigurationConverter;
import java.util.List;
import java.lang.reflect.Array;
import org.codehaus.plexus.util.StringUtils;
import java.util.ArrayList;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.converters.AbstractConfigurationConverter;

public class ArrayConverter extends AbstractConfigurationConverter
{
    public boolean canConvert(final Class type) {
        return type.isArray();
    }
    
    public Object fromConfiguration(final ConverterLookup converterLookup, final PlexusConfiguration configuration, final Class type, final Class baseType, final ClassLoader classLoader, final ExpressionEvaluator expressionEvaluator, final ConfigurationListener listener) throws ComponentConfigurationException {
        final Object retValue = this.fromExpression(configuration, expressionEvaluator, type);
        if (retValue != null) {
            return retValue;
        }
        final List values = new ArrayList();
        for (int i = 0; i < configuration.getChildCount(); ++i) {
            final PlexusConfiguration c = configuration.getChild(i);
            final String configEntry = c.getName();
            final String name = this.fromXML(configEntry);
            Class childType = this.getClassForImplementationHint(null, c, classLoader);
            if (childType == null && name.indexOf(46) > 0) {
                try {
                    childType = classLoader.loadClass(name);
                }
                catch (ClassNotFoundException ex) {}
            }
            if (childType == null) {
                final String baseTypeName = baseType.getName();
                final int lastDot = baseTypeName.lastIndexOf(46);
                String className;
                if (lastDot == -1) {
                    className = name;
                }
                else {
                    final String basePackage = baseTypeName.substring(0, lastDot);
                    className = basePackage + "." + StringUtils.capitalizeFirstLetter(name);
                }
                try {
                    childType = classLoader.loadClass(className);
                }
                catch (ClassNotFoundException ex2) {}
            }
            if (childType == null) {
                childType = type.getComponentType();
            }
            final ConfigurationConverter converter = converterLookup.lookupConverterForType(childType);
            final Object object = converter.fromConfiguration(converterLookup, c, childType, baseType, classLoader, expressionEvaluator, listener);
            values.add(object);
        }
        return values.toArray((Object[])Array.newInstance(type.getComponentType(), 0));
    }
    
    protected Collection getDefaultCollection(final Class collectionType) {
        Collection retValue = null;
        if (List.class.isAssignableFrom(collectionType)) {
            retValue = new ArrayList();
        }
        else if (Set.class.isAssignableFrom(collectionType)) {
            retValue = new HashSet();
        }
        return retValue;
    }
}
