// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator;

import org.codehaus.plexus.component.configurator.converters.composite.ObjectWithFieldsConverter;
import org.codehaus.plexus.component.configurator.converters.ConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.special.ClassRealmConverter;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;

public class BasicComponentConfigurator extends AbstractComponentConfigurator
{
    public void configureComponent(final Object component, final PlexusConfiguration configuration, final ExpressionEvaluator expressionEvaluator, final ClassRealm containerRealm, final ConfigurationListener listener) throws ComponentConfigurationException {
        super.converterLookup.registerConverter(new ClassRealmConverter(containerRealm));
        final ObjectWithFieldsConverter converter = new ObjectWithFieldsConverter();
        converter.processConfiguration(super.converterLookup, component, containerRealm.getClassLoader(), configuration, expressionEvaluator, listener);
    }
}
