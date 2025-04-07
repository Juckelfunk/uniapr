// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator.converters.basic;

import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import java.io.File;

public class FileConverter extends AbstractBasicConverter
{
    public boolean canConvert(final Class type) {
        return type.equals(File.class);
    }
    
    public Object fromString(final String str) {
        return new File(str);
    }
    
    public Object fromConfiguration(final ConverterLookup converterLookup, final PlexusConfiguration configuration, final Class type, final Class baseType, final ClassLoader classLoader, final ExpressionEvaluator expressionEvaluator, final ConfigurationListener listener) throws ComponentConfigurationException {
        final File f = (File)super.fromConfiguration(converterLookup, configuration, type, baseType, classLoader, expressionEvaluator, listener);
        if (f != null) {
            return expressionEvaluator.alignToBaseDirectory(f);
        }
        return null;
    }
}
