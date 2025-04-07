// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.configuration.processor;

import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationProcessor
{
    protected Map handlers;
    
    public ConfigurationProcessor() {
        this.handlers = new HashMap();
    }
    
    public void addConfigurationResourceHandler(final ConfigurationResourceHandler handler) {
        this.handlers.put(handler.getId(), handler);
    }
    
    public PlexusConfiguration process(final PlexusConfiguration configuration, final Map variables) throws ConfigurationResourceNotFoundException, ConfigurationProcessingException {
        final XmlPlexusConfiguration processed = new XmlPlexusConfiguration("configuration");
        this.walk(configuration, processed, variables);
        return processed;
    }
    
    protected void walk(final PlexusConfiguration source, final PlexusConfiguration processed, final Map variables) throws ConfigurationResourceNotFoundException, ConfigurationProcessingException {
        final PlexusConfiguration[] children = source.getChildren();
        for (int i = 0; i < children.length; ++i) {
            final PlexusConfiguration child = children[i];
            final int count = child.getChildCount();
            if (count > 0) {
                final XmlPlexusConfiguration processedChild = new XmlPlexusConfiguration(child.getName());
                this.copyAttributes(child, processedChild);
                processed.addChild(processedChild);
                this.walk(child, processedChild, variables);
            }
            else {
                final String elementName = child.getName();
                if (this.handlers.containsKey(elementName)) {
                    final ConfigurationResourceHandler handler = this.handlers.get(elementName);
                    final PlexusConfiguration[] configurations = handler.handleRequest(this.createHandlerParameters(child, variables));
                    for (int j = 0; j < configurations.length; ++j) {
                        processed.addChild(configurations[j]);
                    }
                }
                else {
                    processed.addChild(child);
                }
            }
        }
    }
    
    protected Map createHandlerParameters(final PlexusConfiguration c, final Map variables) {
        final Map parameters = new HashMap();
        final String[] parameterNames = c.getAttributeNames();
        for (int i = 0; i < parameterNames.length; ++i) {
            final String key = parameterNames[i];
            final String value = StringUtils.interpolate(c.getAttribute(key, null), variables);
            parameters.put(key, value);
        }
        return parameters;
    }
    
    private void copyAttributes(final PlexusConfiguration source, final XmlPlexusConfiguration target) {
        final String[] names = source.getAttributeNames();
        for (int i = 0; i < names.length; ++i) {
            target.setAttribute(names[i], source.getAttribute(names[i], null));
        }
    }
}
