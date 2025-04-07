// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator.converters;

import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;

public interface ConfigurationConverter
{
    boolean canConvert(final Class p0);
    
    Object fromConfiguration(final ConverterLookup p0, final PlexusConfiguration p1, final Class p2, final Class p3, final ClassLoader p4, final ExpressionEvaluator p5) throws ComponentConfigurationException;
    
    Object fromConfiguration(final ConverterLookup p0, final PlexusConfiguration p1, final Class p2, final Class p3, final ClassLoader p4, final ExpressionEvaluator p5, final ConfigurationListener p6) throws ComponentConfigurationException;
}
