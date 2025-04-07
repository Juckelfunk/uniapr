// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator;

import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.component.configurator.expression.DefaultExpressionEvaluator;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.component.configurator.converters.lookup.DefaultConverterLookup;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;

public abstract class AbstractComponentConfigurator implements ComponentConfigurator
{
    protected ConverterLookup converterLookup;
    
    public AbstractComponentConfigurator() {
        this.converterLookup = new DefaultConverterLookup();
    }
    
    public void configureComponent(final Object component, final PlexusConfiguration configuration, final ClassRealm containerRealm) throws ComponentConfigurationException {
        this.configureComponent(component, configuration, new DefaultExpressionEvaluator(), containerRealm);
    }
    
    public void configureComponent(final Object component, final PlexusConfiguration configuration, final ExpressionEvaluator expressionEvaluator, final ClassRealm containerRealm) throws ComponentConfigurationException {
        this.configureComponent(component, configuration, expressionEvaluator, containerRealm, null);
    }
    
    public void configureComponent(final Object component, final PlexusConfiguration configuration, final ExpressionEvaluator expressionEvaluator, final ClassRealm containerRealm, final ConfigurationListener listener) throws ComponentConfigurationException {
        this.configureComponent(component, configuration, expressionEvaluator, containerRealm);
    }
}
