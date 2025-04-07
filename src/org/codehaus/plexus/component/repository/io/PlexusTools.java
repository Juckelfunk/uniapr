// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.repository.io;

import org.codehaus.plexus.component.repository.ComponentDependency;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import java.io.StringReader;
import java.io.IOException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import java.io.Reader;

public class PlexusTools
{
    public static PlexusConfiguration buildConfiguration(final String resourceName, final Reader configuration) throws PlexusConfigurationException {
        try {
            return new XmlPlexusConfiguration(Xpp3DomBuilder.build(configuration));
        }
        catch (XmlPullParserException e) {
            throw new PlexusConfigurationException("Failed to parse configuration resource: '" + resourceName + "'\nError was: '" + e.getLocalizedMessage() + "'", e);
        }
        catch (IOException e2) {
            throw new PlexusConfigurationException("IO error building configuration from: " + resourceName, e2);
        }
    }
    
    public static PlexusConfiguration buildConfiguration(final String configuration) throws PlexusConfigurationException {
        return buildConfiguration("<String Memory Resource>", new StringReader(configuration));
    }
    
    public static ComponentDescriptor buildComponentDescriptor(final String configuration) throws PlexusConfigurationException {
        return buildComponentDescriptor(buildConfiguration(configuration));
    }
    
    public static ComponentDescriptor buildComponentDescriptor(final PlexusConfiguration configuration) throws PlexusConfigurationException {
        final ComponentDescriptor cd = new ComponentDescriptor();
        cd.setRole(configuration.getChild("role").getValue());
        cd.setRoleHint(configuration.getChild("role-hint").getValue());
        cd.setImplementation(configuration.getChild("implementation").getValue());
        cd.setVersion(configuration.getChild("version").getValue());
        cd.setComponentType(configuration.getChild("component-type").getValue());
        cd.setInstantiationStrategy(configuration.getChild("instantiation-strategy").getValue());
        cd.setLifecycleHandler(configuration.getChild("lifecycle-handler").getValue());
        cd.setComponentProfile(configuration.getChild("component-profile").getValue());
        cd.setComponentComposer(configuration.getChild("component-composer").getValue());
        cd.setComponentConfigurator(configuration.getChild("component-configurator").getValue());
        cd.setComponentFactory(configuration.getChild("component-factory").getValue());
        cd.setDescription(configuration.getChild("description").getValue());
        cd.setAlias(configuration.getChild("alias").getValue());
        final String s = configuration.getChild("isolated-realm").getValue();
        if (s != null) {
            cd.setIsolatedRealm(s.equals("true"));
        }
        cd.setConfiguration(configuration.getChild("configuration"));
        final PlexusConfiguration[] requirements = configuration.getChild("requirements").getChildren("requirement");
        for (int i = 0; i < requirements.length; ++i) {
            final PlexusConfiguration requirement = requirements[i];
            final ComponentRequirement cr = new ComponentRequirement();
            cr.setRole(requirement.getChild("role").getValue());
            cr.setRoleHint(requirement.getChild("role-hint").getValue());
            cr.setFieldName(requirement.getChild("field-name").getValue());
            cd.addRequirement(cr);
        }
        return cd;
    }
    
    public static ComponentSetDescriptor buildComponentSet(final PlexusConfiguration c) throws PlexusConfigurationException {
        final ComponentSetDescriptor csd = new ComponentSetDescriptor();
        final PlexusConfiguration[] components = c.getChild("components").getChildren("component");
        for (int i = 0; i < components.length; ++i) {
            final PlexusConfiguration component = components[i];
            csd.addComponentDescriptor(buildComponentDescriptor(component));
        }
        final PlexusConfiguration[] dependencies = c.getChild("dependencies").getChildren("dependency");
        for (int j = 0; j < dependencies.length; ++j) {
            final PlexusConfiguration d = dependencies[j];
            final ComponentDependency cd = new ComponentDependency();
            cd.setArtifactId(d.getChild("artifact-id").getValue());
            cd.setGroupId(d.getChild("group-id").getValue());
            final String type = d.getChild("type").getValue();
            if (type != null) {
                cd.setType(type);
            }
            cd.setVersion(d.getChild("version").getValue());
            csd.addDependency(cd);
        }
        return csd;
    }
}
