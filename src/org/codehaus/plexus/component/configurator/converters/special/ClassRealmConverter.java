// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator.converters.special;

import org.codehaus.plexus.component.configurator.converters.ConfigurationConverter;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.component.configurator.converters.AbstractConfigurationConverter;

public class ClassRealmConverter extends AbstractConfigurationConverter
{
    public static final String ROLE;
    private ClassRealm classRealm;
    
    public ClassRealmConverter(final ClassRealm classRealm) {
        this.setClassRealm(classRealm);
    }
    
    public void setClassRealm(final ClassRealm classRealm) {
        this.classRealm = classRealm;
    }
    
    public boolean canConvert(final Class type) {
        return ClassRealm.class.isAssignableFrom(type);
    }
    
    public Object fromConfiguration(final ConverterLookup converterLookup, final PlexusConfiguration configuration, final Class type, final Class baseType, final ClassLoader classLoader, final ExpressionEvaluator expressionEvaluator, final ConfigurationListener listener) throws ComponentConfigurationException {
        final Object retValue = this.fromExpression(configuration, expressionEvaluator, type);
        if (retValue != null) {
            return retValue;
        }
        return this.classRealm;
    }
    
    static {
        ROLE = ConfigurationConverter.class.getName();
    }
}
