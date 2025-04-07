// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.configuration;

import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;

public class PlexusConfigurationMerger
{
    public static PlexusConfiguration merge(final PlexusConfiguration user, final PlexusConfiguration system) {
        final XmlPlexusConfiguration mergedConfiguration = new XmlPlexusConfiguration("plexus");
        final PlexusConfiguration loadOnStart = user.getChild("load-on-start");
        if (loadOnStart.getChildCount() != 0) {
            mergedConfiguration.addChild(loadOnStart);
        }
        final PlexusConfiguration systemProperties = user.getChild("system-properties");
        if (systemProperties.getChildCount() != 0) {
            mergedConfiguration.addChild(systemProperties);
        }
        final PlexusConfiguration[] configurationsDirectories = user.getChildren("configurations-directory");
        if (configurationsDirectories.length != 0) {
            for (int i = 0; i < configurationsDirectories.length; ++i) {
                mergedConfiguration.addChild(configurationsDirectories[i]);
            }
        }
        final PlexusConfiguration logging = user.getChild("logging");
        if (logging.getChildCount() != 0) {
            mergedConfiguration.addChild(logging);
        }
        else {
            mergedConfiguration.addChild(system.getChild("logging"));
        }
        final PlexusConfiguration componentRepository = user.getChild("component-repository");
        if (componentRepository.getChildCount() != 0) {
            mergedConfiguration.addChild(componentRepository);
        }
        else {
            mergedConfiguration.addChild(system.getChild("component-repository"));
        }
        copyResources(system, mergedConfiguration);
        copyResources(user, mergedConfiguration);
        mergedConfiguration.addChild(system.getChild("component-manager-manager"));
        final PlexusConfiguration componentDiscovererManager = user.getChild("component-discoverer-manager");
        if (componentDiscovererManager.getChildCount() != 0) {
            mergedConfiguration.addChild(componentDiscovererManager);
            copyComponentDiscoverers(system.getChild("component-discoverer-manager"), componentDiscovererManager);
        }
        else {
            mergedConfiguration.addChild(system.getChild("component-discoverer-manager"));
        }
        final PlexusConfiguration componentFactoryManager = user.getChild("component-factory-manager");
        if (componentFactoryManager.getChildCount() != 0) {
            mergedConfiguration.addChild(componentFactoryManager);
            copyComponentFactories(system.getChild("component-factory-manager"), componentFactoryManager);
        }
        else {
            mergedConfiguration.addChild(system.getChild("component-factory-manager"));
        }
        final PlexusConfiguration lifecycleHandlerManager = user.getChild("lifecycle-handler-manager");
        if (lifecycleHandlerManager.getChildCount() != 0) {
            mergedConfiguration.addChild(lifecycleHandlerManager);
            copyLifecycles(system.getChild("lifecycle-handler-manager"), lifecycleHandlerManager);
        }
        else {
            mergedConfiguration.addChild(system.getChild("lifecycle-handler-manager"));
        }
        final PlexusConfiguration componentComposerManager = user.getChild("component-composer-manager");
        if (componentComposerManager.getChildCount() != 0) {
            mergedConfiguration.addChild(componentComposerManager);
            copyComponentComposers(system.getChild("component-composer-manager"), componentComposerManager);
        }
        else {
            mergedConfiguration.addChild(system.getChild("component-composer-manager"));
        }
        final PlexusConfiguration components = system.getChild("components");
        mergedConfiguration.addChild(components);
        copyComponents(user.getChild("components"), components);
        return mergedConfiguration;
    }
    
    private static void copyResources(final PlexusConfiguration source, final PlexusConfiguration destination) {
        final PlexusConfiguration[] handlers = source.getChild("resources").getChildren();
        final XmlPlexusConfiguration dest = (XmlPlexusConfiguration)destination.getChild("resources");
        for (int i = 0; i < handlers.length; ++i) {
            dest.addChild(handlers[i]);
        }
    }
    
    private static void copyComponentDiscoverers(final PlexusConfiguration source, final PlexusConfiguration destination) {
        final PlexusConfiguration[] handlers = source.getChild("component-discoverers").getChildren("component-discoverer");
        final XmlPlexusConfiguration dest = (XmlPlexusConfiguration)destination.getChild("component-discoverers");
        for (int i = 0; i < handlers.length; ++i) {
            dest.addChild(handlers[i]);
        }
    }
    
    private static void copyComponentFactories(final PlexusConfiguration source, final PlexusConfiguration destination) {
        final PlexusConfiguration[] handlers = source.getChild("component-factories").getChildren("component-factory");
        final XmlPlexusConfiguration dest = (XmlPlexusConfiguration)destination.getChild("component-factories");
        for (int i = 0; i < handlers.length; ++i) {
            dest.addChild(handlers[i]);
        }
    }
    
    private static void copyComponentComposers(final PlexusConfiguration source, final PlexusConfiguration destination) {
        final PlexusConfiguration[] composers = source.getChild("component-composers").getChildren("component-composer");
        final XmlPlexusConfiguration dest = (XmlPlexusConfiguration)destination.getChild("component-composers");
        for (int i = 0; i < composers.length; ++i) {
            dest.addChild(composers[i]);
        }
    }
    
    private static void copyLifecycles(final PlexusConfiguration source, final PlexusConfiguration destination) {
        final PlexusConfiguration[] handlers = source.getChild("lifecycle-handlers").getChildren("lifecycle-handler");
        final XmlPlexusConfiguration dest = (XmlPlexusConfiguration)destination.getChild("lifecycle-handlers");
        for (int i = 0; i < handlers.length; ++i) {
            dest.addChild(handlers[i]);
        }
    }
    
    private static void copyComponents(final PlexusConfiguration source, final PlexusConfiguration destination) {
        final PlexusConfiguration[] components = source.getChildren("component");
        for (int i = 0; i < components.length; ++i) {
            destination.addChild(components[i]);
        }
    }
}
