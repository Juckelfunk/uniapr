// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator.converters;

import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.configuration.PlexusConfiguration;

public abstract class AbstractConfigurationConverter implements ConfigurationConverter
{
    private static final String IMPLEMENTATION = "implementation";
    
    protected Class getClassForImplementationHint(final Class type, final PlexusConfiguration configuration, final ClassLoader classLoader) throws ComponentConfigurationException {
        Class retValue = type;
        final String implementation = configuration.getAttribute("implementation", null);
        if (implementation != null) {
            try {
                retValue = classLoader.loadClass(implementation);
            }
            catch (ClassNotFoundException e) {
                final String msg = "Class name which was explicitly given in configuration using 'implementation' attribute: '" + implementation + "' cannot be loaded";
                throw new ComponentConfigurationException(msg, e);
            }
        }
        return retValue;
    }
    
    protected Class loadClass(final String classname, final ClassLoader classLoader) throws ComponentConfigurationException {
        Class retValue;
        try {
            retValue = classLoader.loadClass(classname);
        }
        catch (ClassNotFoundException e) {
            throw new ComponentConfigurationException("Error loading class '" + classname + "'", e);
        }
        return retValue;
    }
    
    protected Object instantiateObject(final String classname, final ClassLoader classLoader) throws ComponentConfigurationException {
        final Class clazz = this.loadClass(classname, classLoader);
        return this.instantiateObject(clazz);
    }
    
    protected Object instantiateObject(final Class clazz) throws ComponentConfigurationException {
        try {
            final Object retValue = clazz.newInstance();
            return retValue;
        }
        catch (IllegalAccessException e) {
            throw new ComponentConfigurationException("Class '" + clazz.getName() + "' cannot be instantiated", e);
        }
        catch (InstantiationException e2) {
            throw new ComponentConfigurationException("Class '" + clazz.getName() + "' cannot be instantiated", e2);
        }
    }
    
    protected String fromXML(final String elementName) {
        return StringUtils.lowercaseFirstLetter(StringUtils.removeAndHump(elementName, "-"));
    }
    
    protected String toXML(final String fieldName) {
        return StringUtils.addAndDeHump(fieldName);
    }
    
    protected Object fromExpression(final PlexusConfiguration configuration, final ExpressionEvaluator expressionEvaluator, final Class type) throws ComponentConfigurationException {
        final Object v = this.fromExpression(configuration, expressionEvaluator);
        if (v != null && !type.isAssignableFrom(v.getClass())) {
            final String msg = "Cannot assign configuration entry '" + configuration.getName() + "' to '" + type + "' from '" + configuration.getValue(null) + "', which is of type " + v.getClass();
            throw new ComponentConfigurationException(configuration, msg);
        }
        return v;
    }
    
    protected Object fromExpression(final PlexusConfiguration configuration, final ExpressionEvaluator expressionEvaluator) throws ComponentConfigurationException {
        Object v = null;
        String value = configuration.getValue(null);
        if (value != null && value.length() > 0) {
            try {
                v = expressionEvaluator.evaluate(value);
            }
            catch (ExpressionEvaluationException e) {
                final String msg = "Error evaluating the expression '" + value + "' for configuration value '" + configuration.getName() + "'";
                throw new ComponentConfigurationException(configuration, msg, e);
            }
        }
        if (v == null) {
            value = configuration.getAttribute("default-value", null);
            if (value != null && value.length() > 0) {
                try {
                    v = expressionEvaluator.evaluate(value);
                }
                catch (ExpressionEvaluationException e) {
                    final String msg = "Error evaluating the expression '" + value + "' for configuration value '" + configuration.getName() + "'";
                    throw new ComponentConfigurationException(configuration, msg, e);
                }
            }
        }
        return v;
    }
    
    public Object fromConfiguration(final ConverterLookup converterLookup, final PlexusConfiguration configuration, final Class type, final Class baseType, final ClassLoader classLoader, final ExpressionEvaluator expressionEvaluator) throws ComponentConfigurationException {
        return this.fromConfiguration(converterLookup, configuration, type, baseType, classLoader, expressionEvaluator, null);
    }
}
