// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator;

import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.configuration.PlexusConfiguration;

public interface ComponentConfigurator
{
    public static final String ROLE = ((ComponentConfigurator$1.class$org$codehaus$plexus$component$configurator$ComponentConfigurator == null) ? (ComponentConfigurator$1.class$org$codehaus$plexus$component$configurator$ComponentConfigurator = ComponentConfigurator$1.class$("org.codehaus.plexus.component.configurator.ComponentConfigurator")) : ComponentConfigurator$1.class$org$codehaus$plexus$component$configurator$ComponentConfigurator).getName();
    
    void configureComponent(final Object p0, final PlexusConfiguration p1, final ClassRealm p2) throws ComponentConfigurationException;
    
    void configureComponent(final Object p0, final PlexusConfiguration p1, final ExpressionEvaluator p2, final ClassRealm p3) throws ComponentConfigurationException;
    
    void configureComponent(final Object p0, final PlexusConfiguration p1, final ExpressionEvaluator p2, final ClassRealm p3, final ConfigurationListener p4) throws ComponentConfigurationException;
}
