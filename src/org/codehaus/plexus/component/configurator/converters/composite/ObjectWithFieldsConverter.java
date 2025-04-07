// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator.converters.composite;

import java.util.Collection;
import java.util.Map;
import java.util.Dictionary;
import org.codehaus.plexus.component.configurator.converters.ComponentValueSetter;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.converters.AbstractConfigurationConverter;

public class ObjectWithFieldsConverter extends AbstractConfigurationConverter
{
    public boolean canConvert(final Class type) {
        boolean retValue = true;
        if (Dictionary.class.isAssignableFrom(type)) {
            retValue = false;
        }
        else if (Map.class.isAssignableFrom(type)) {
            retValue = false;
        }
        else if (Collection.class.isAssignableFrom(type)) {
            retValue = false;
        }
        return retValue;
    }
    
    public Object fromConfiguration(final ConverterLookup converterLookup, final PlexusConfiguration configuration, final Class type, final Class baseType, final ClassLoader classLoader, final ExpressionEvaluator expressionEvaluator, final ConfigurationListener listener) throws ComponentConfigurationException {
        Object retValue = this.fromExpression(configuration, expressionEvaluator, type);
        if (retValue == null) {
            try {
                final Class implementation = this.getClassForImplementationHint(type, configuration, classLoader);
                retValue = this.instantiateObject(implementation);
                this.processConfiguration(converterLookup, retValue, classLoader, configuration, expressionEvaluator, listener);
            }
            catch (ComponentConfigurationException e) {
                if (e.getFailedConfiguration() == null) {
                    e.setFailedConfiguration(configuration);
                }
                throw e;
            }
        }
        return retValue;
    }
    
    public void processConfiguration(final ConverterLookup converterLookup, final Object object, final ClassLoader classLoader, final PlexusConfiguration configuration) throws ComponentConfigurationException {
        this.processConfiguration(converterLookup, object, classLoader, configuration, null);
    }
    
    public void processConfiguration(final ConverterLookup converterLookup, final Object object, final ClassLoader classLoader, final PlexusConfiguration configuration, final ExpressionEvaluator expressionEvaluator) throws ComponentConfigurationException {
        this.processConfiguration(converterLookup, object, classLoader, configuration, expressionEvaluator, null);
    }
    
    public void processConfiguration(final ConverterLookup converterLookup, final Object object, final ClassLoader classLoader, final PlexusConfiguration configuration, final ExpressionEvaluator expressionEvaluator, final ConfigurationListener listener) throws ComponentConfigurationException {
        for (int items = configuration.getChildCount(), i = 0; i < items; ++i) {
            final PlexusConfiguration childConfiguration = configuration.getChild(i);
            final String elementName = childConfiguration.getName();
            final ComponentValueSetter valueSetter = new ComponentValueSetter(this.fromXML(elementName), object, converterLookup, listener);
            valueSetter.configure(childConfiguration, classLoader, expressionEvaluator);
        }
    }
}
