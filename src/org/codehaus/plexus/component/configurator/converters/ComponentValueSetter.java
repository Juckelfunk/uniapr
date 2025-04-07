// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator.converters;

import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import java.lang.reflect.InvocationTargetException;
import org.codehaus.plexus.util.ReflectionUtils;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;

public class ComponentValueSetter
{
    private Object object;
    private String fieldName;
    private ConverterLookup lookup;
    private Method setter;
    private Class setterParamType;
    private ConfigurationConverter setterTypeConverter;
    private Field field;
    private Class fieldType;
    private ConfigurationConverter fieldTypeConverter;
    private ConfigurationListener listener;
    
    public ComponentValueSetter(final String fieldName, final Object object, final ConverterLookup lookup) throws ComponentConfigurationException {
        this(fieldName, object, lookup, null);
    }
    
    public ComponentValueSetter(final String fieldName, final Object object, final ConverterLookup lookup, final ConfigurationListener listener) throws ComponentConfigurationException {
        this.fieldName = fieldName;
        this.object = object;
        this.lookup = lookup;
        this.listener = listener;
        if (object == null) {
            throw new ComponentConfigurationException("Component is null");
        }
        this.initSetter();
        this.initField();
        if (this.setter == null && this.field == null) {
            throw new ComponentConfigurationException("Cannot find setter nor field in " + object.getClass().getName() + " for '" + fieldName + "'");
        }
        if (this.setterTypeConverter == null && this.fieldTypeConverter == null) {
            throw new ComponentConfigurationException("Cannot find converter for " + this.setterParamType.getName() + ((this.fieldType != null && !this.fieldType.equals(this.setterParamType)) ? (" or " + this.fieldType.getName()) : ""));
        }
    }
    
    private void initSetter() {
        this.setter = ReflectionUtils.getSetter(this.fieldName, this.object.getClass());
        if (this.setter == null) {
            return;
        }
        this.setterParamType = this.setter.getParameterTypes()[0];
        try {
            this.setterTypeConverter = this.lookup.lookupConverterForType(this.setterParamType);
        }
        catch (ComponentConfigurationException ex) {}
    }
    
    private void initField() {
        this.field = ReflectionUtils.getFieldByNameIncludingSuperclasses(this.fieldName, this.object.getClass());
        if (this.field == null) {
            return;
        }
        this.fieldType = this.field.getType();
        try {
            this.fieldTypeConverter = this.lookup.lookupConverterForType(this.fieldType);
        }
        catch (ComponentConfigurationException ex) {}
    }
    
    private void setValueUsingField(final Object value) throws ComponentConfigurationException {
        final String exceptionInfo = this.object.getClass().getName() + "." + this.field.getName() + "; type: " + value.getClass().getName();
        try {
            final boolean wasAccessible = this.field.isAccessible();
            if (!wasAccessible) {
                this.field.setAccessible(true);
            }
            if (this.listener != null) {
                this.listener.notifyFieldChangeUsingReflection(this.fieldName, value, this.object);
            }
            this.field.set(this.object, value);
            if (!wasAccessible) {
                this.field.setAccessible(false);
            }
        }
        catch (IllegalAccessException e) {
            throw new ComponentConfigurationException("Cannot access field: " + exceptionInfo, e);
        }
        catch (IllegalArgumentException e2) {
            throw new ComponentConfigurationException("Cannot assign value '" + value + "' to field: " + exceptionInfo, e2);
        }
    }
    
    private void setValueUsingSetter(final Object value) throws ComponentConfigurationException {
        if (this.setterParamType == null || this.setter == null) {
            throw new ComponentConfigurationException("No setter found");
        }
        final String exceptionInfo = this.object.getClass().getName() + "." + this.setter.getName() + "( " + this.setterParamType.getClass().getName() + " )";
        if (this.listener != null) {
            this.listener.notifyFieldChangeUsingSetter(this.fieldName, value, this.object);
        }
        try {
            this.setter.invoke(this.object, value);
        }
        catch (IllegalAccessException e) {
            throw new ComponentConfigurationException("Cannot access method: " + exceptionInfo, e);
        }
        catch (IllegalArgumentException e2) {
            throw new ComponentConfigurationException("Invalid parameter supplied while setting '" + value + "' to " + exceptionInfo, e2);
        }
        catch (InvocationTargetException e3) {
            throw new ComponentConfigurationException("Setter " + exceptionInfo + " threw exception when called with parameter '" + value + "': " + e3.getTargetException().getMessage(), e3);
        }
    }
    
    public void configure(final PlexusConfiguration config, final ClassLoader cl, final ExpressionEvaluator evaluator) throws ComponentConfigurationException {
        Object value = null;
        if (this.setterTypeConverter != null) {
            try {
                value = this.setterTypeConverter.fromConfiguration(this.lookup, config, this.setterParamType, this.object.getClass(), cl, evaluator, this.listener);
                if (value != null) {
                    this.setValueUsingSetter(value);
                    return;
                }
            }
            catch (ComponentConfigurationException e) {
                if (this.fieldTypeConverter == null || this.fieldTypeConverter.getClass().equals(this.setterTypeConverter.getClass())) {
                    throw e;
                }
            }
        }
        ComponentConfigurationException savedEx = null;
        if (value != null) {
            try {
                this.setValueUsingField(value);
                return;
            }
            catch (ComponentConfigurationException e2) {
                savedEx = e2;
            }
        }
        value = this.fieldTypeConverter.fromConfiguration(this.lookup, config, this.fieldType, this.object.getClass(), cl, evaluator, this.listener);
        if (value != null) {
            this.setValueUsingField(value);
        }
        else if (savedEx != null) {
            throw savedEx;
        }
    }
}
