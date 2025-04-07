// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.composition;

import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import java.util.HashSet;
import java.util.Collections;
import org.codehaus.plexus.util.StringUtils;
import java.util.Iterator;
import java.util.Collection;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import java.util.LinkedList;
import org.codehaus.plexus.component.MapOrientedComponent;
import java.util.List;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

public class MapOrientedComponentComposer extends AbstractComponentComposer
{
    private static final String SINGLE_MAPPING_TYPE = "single";
    private static final String MAP_MAPPING_TYPE = "map";
    private static final String SET_MAPPING_TYPE = "set";
    private static final String DEFAULT_MAPPING_TYPE = "single";
    
    public List assembleComponent(final Object component, final ComponentDescriptor componentDescriptor, final PlexusContainer container) throws CompositionException {
        if (!(component instanceof MapOrientedComponent)) {
            throw new CompositionException("Cannot compose component: " + component.getClass().getName() + "; it does not implement " + MapOrientedComponent.class.getName());
        }
        final List retValue = new LinkedList();
        final List requirements = componentDescriptor.getRequirements();
        for (final ComponentRequirement requirement : requirements) {
            final List descriptors = this.addRequirement((MapOrientedComponent)component, container, requirement);
            retValue.addAll(descriptors);
        }
        return retValue;
    }
    
    private List addRequirement(final MapOrientedComponent component, final PlexusContainer container, final ComponentRequirement requirement) throws CompositionException {
        try {
            final String role = requirement.getRole();
            final String hint = requirement.getRoleHint();
            final String mappingType = requirement.getFieldMappingType();
            Object value = null;
            List retValue;
            if (StringUtils.isNotEmpty(hint)) {
                final String key = requirement.getRequirementKey();
                value = container.lookup(key);
                final ComponentDescriptor componentDescriptor = container.getComponentDescriptor(key);
                retValue = Collections.singletonList(componentDescriptor);
            }
            else if ("single".equals(mappingType)) {
                final String key = requirement.getRequirementKey();
                value = container.lookup(key);
                final ComponentDescriptor componentDescriptor = container.getComponentDescriptor(key);
                retValue = Collections.singletonList(componentDescriptor);
            }
            else if ("map".equals(mappingType)) {
                value = container.lookupMap(role);
                retValue = container.getComponentDescriptorList(role);
            }
            else if ("set".equals(mappingType)) {
                value = new HashSet(container.lookupList(role));
                retValue = container.getComponentDescriptorList(role);
            }
            else {
                final String key = requirement.getRequirementKey();
                value = container.lookup(key);
                final ComponentDescriptor componentDescriptor = container.getComponentDescriptor(key);
                retValue = Collections.singletonList(componentDescriptor);
            }
            component.addComponentRequirement(requirement, value);
            return retValue;
        }
        catch (ComponentLookupException e) {
            throw new CompositionException("Composition failed in object of type " + component.getClass().getName() + " because the requirement " + requirement + " was missing", e);
        }
        catch (ComponentConfigurationException e2) {
            throw new CompositionException("Composition failed in object of type " + component.getClass().getName() + " because the requirement " + requirement + " cannot be set on the component.", e2);
        }
    }
}
