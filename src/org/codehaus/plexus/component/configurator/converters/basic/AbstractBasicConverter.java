// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator.converters.basic;

import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.converters.AbstractConfigurationConverter;

public abstract class AbstractBasicConverter extends AbstractConfigurationConverter
{
    protected abstract Object fromString(final String p0) throws ComponentConfigurationException;
    
    public Object fromConfiguration(final ConverterLookup converterLookup, final PlexusConfiguration configuration, final Class type, final Class baseType, final ClassLoader classLoader, final ExpressionEvaluator expressionEvaluator, final ConfigurationListener listener) throws ComponentConfigurationException {
        if (configuration.getChildCount() > 0) {
            throw new ComponentConfigurationException("When configuring a basic element the configuration cannot contain any child elements. Configuration element '" + configuration.getName() + "'.");
        }
        Object retValue = this.fromExpression(configuration, expressionEvaluator);
        if (retValue instanceof String) {
            retValue = this.fromString((String)retValue);
        }
        return retValue;
    }
}
