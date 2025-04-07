// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator;

import java.util.Map;
import org.codehaus.plexus.component.configurator.converters.composite.MapConverter;
import org.codehaus.plexus.component.MapOrientedComponent;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;

public class MapOrientedComponentConfigurator extends AbstractComponentConfigurator
{
    public void configureComponent(final Object component, final PlexusConfiguration configuration, final ExpressionEvaluator expressionEvaluator, final ClassRealm containerRealm, final ConfigurationListener listener) throws ComponentConfigurationException {
        if (!(component instanceof MapOrientedComponent)) {
            throw new ComponentConfigurationException("This configurator can only process implementations of " + MapOrientedComponent.class.getName());
        }
        final MapConverter converter = new MapConverter();
        final Map context = (Map)converter.fromConfiguration(super.converterLookup, configuration, null, null, containerRealm.getClassLoader(), expressionEvaluator, listener);
        ((MapOrientedComponent)component).setComponentConfiguration(context);
    }
}
