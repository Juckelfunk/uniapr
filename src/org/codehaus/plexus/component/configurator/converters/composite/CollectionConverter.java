// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator.converters.composite;

import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.ArrayList;
import org.codehaus.plexus.component.configurator.converters.ConfigurationConverter;
import java.util.Collection;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import java.lang.reflect.Modifier;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.converters.AbstractConfigurationConverter;

public class CollectionConverter extends AbstractConfigurationConverter
{
    public boolean canConvert(final Class type) {
        return Collection.class.isAssignableFrom(type) && !Map.class.isAssignableFrom(type);
    }
    
    public Object fromConfiguration(final ConverterLookup converterLookup, final PlexusConfiguration configuration, final Class type, final Class baseType, final ClassLoader classLoader, final ExpressionEvaluator expressionEvaluator, final ConfigurationListener listener) throws ComponentConfigurationException {
        Object retValue = this.fromExpression(configuration, expressionEvaluator, type);
        if (retValue != null) {
            return retValue;
        }
        final Class implementation = this.getClassForImplementationHint(null, configuration, classLoader);
        if (implementation != null) {
            retValue = this.instantiateObject(implementation);
        }
        else {
            final int modifiers = type.getModifiers();
            if (Modifier.isAbstract(modifiers)) {
                retValue = this.getDefaultCollection(type);
            }
            else {
                try {
                    retValue = type.newInstance();
                }
                catch (IllegalAccessException e) {
                    final String msg = "An attempt to convert configuration entry " + configuration.getName() + "' into " + type + " object failed: " + e.getMessage();
                    throw new ComponentConfigurationException(msg, e);
                }
                catch (InstantiationException e2) {
                    final String msg = "An attempt to convert configuration entry " + configuration.getName() + "' into " + type + " object failed: " + e2.getMessage();
                    throw new ComponentConfigurationException(msg, e2);
                }
            }
        }
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
                catch (ClassNotFoundException e3) {
                    if (c.getChildCount() != 0) {
                        throw new ComponentConfigurationException("Error loading class '" + className + "'", e3);
                    }
                    childType = String.class;
                }
            }
            final ConfigurationConverter converter = converterLookup.lookupConverterForType(childType);
            final Object object = converter.fromConfiguration(converterLookup, c, childType, baseType, classLoader, expressionEvaluator, listener);
            final Collection collection = (Collection)retValue;
            collection.add(object);
        }
        return retValue;
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
