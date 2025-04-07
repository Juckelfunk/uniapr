// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.manager;

import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.converters.composite.ObjectWithFieldsConverter;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.component.configurator.AbstractComponentConfigurator;

public class WagonComponentConfigurator extends AbstractComponentConfigurator
{
    @Override
    public void configureComponent(final Object component, final PlexusConfiguration configuration, final ExpressionEvaluator expressionEvaluator, final ClassRealm containerRealm, final ConfigurationListener listener) throws ComponentConfigurationException {
        final ObjectWithFieldsConverter converter = new ObjectWithFieldsConverter();
        converter.processConfiguration(this.converterLookup, component, containerRealm.getClassLoader(), configuration, expressionEvaluator, listener);
    }
}
