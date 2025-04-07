// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.composition;

import java.util.Set;
import java.util.Map;
import java.lang.reflect.Method;
import java.beans.Statement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import java.util.ArrayList;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.beans.PropertyDescriptor;
import java.beans.BeanInfo;
import java.util.Collection;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import java.util.LinkedList;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.util.List;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

public class SetterComponentComposer extends AbstractComponentComposer
{
    public List assembleComponent(final Object component, final ComponentDescriptor descriptor, final PlexusContainer container) throws CompositionException, UndefinedComponentComposerException {
        final List requirements = descriptor.getRequirements();
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(component.getClass());
        }
        catch (IntrospectionException e) {
            this.reportErrorFailedToIntrospect(descriptor);
        }
        final List retValue = new LinkedList();
        final PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (final ComponentRequirement requirement : requirements) {
            final PropertyDescriptor propertyDescriptor = this.findMatchingPropertyDescriptor(requirement, propertyDescriptors);
            if (propertyDescriptor != null) {
                final List descriptors = this.setProperty(component, descriptor, requirement, propertyDescriptor, container);
                retValue.addAll(descriptors);
            }
            else {
                this.reportErrorNoSuchProperty(descriptor, requirement);
            }
        }
        return retValue;
    }
    
    private List setProperty(final Object component, final ComponentDescriptor descriptor, final ComponentRequirement requirement, final PropertyDescriptor propertyDescriptor, final PlexusContainer container) throws CompositionException {
        List retValue = null;
        final Method writeMethod = propertyDescriptor.getWriteMethod();
        final String role = requirement.getRole();
        final Object[] params = { null };
        final Class propertyType = propertyDescriptor.getPropertyType();
        try {
            if (propertyType.isArray()) {
                final Map dependencies = container.lookupMap(role);
                final Object[] array = (Object[])Array.newInstance(propertyType, dependencies.size());
                retValue = container.getComponentDescriptorList(role);
                params[0] = dependencies.entrySet().toArray(array);
            }
            else if (Map.class.isAssignableFrom(propertyType)) {
                final Map dependencies = container.lookupMap(role);
                retValue = container.getComponentDescriptorList(role);
                params[0] = dependencies;
            }
            else if (List.class.isAssignableFrom(propertyType)) {
                retValue = container.getComponentDescriptorList(role);
                params[0] = container.lookupList(role);
            }
            else if (Set.class.isAssignableFrom(propertyType)) {
                final Map dependencies = container.lookupMap(role);
                retValue = container.getComponentDescriptorList(role);
                params[0] = dependencies.entrySet();
            }
            else {
                final String key = requirement.getRequirementKey();
                final Object dependency = container.lookup(key);
                final ComponentDescriptor componentDescriptor = container.getComponentDescriptor(key);
                retValue = new ArrayList(1);
                retValue.add(componentDescriptor);
                params[0] = dependency;
            }
        }
        catch (ComponentLookupException e) {
            this.reportErrorCannotLookupRequiredComponent(descriptor, requirement, e);
        }
        final Statement statement = new Statement(component, writeMethod.getName(), params);
        try {
            statement.execute();
        }
        catch (Exception e2) {
            this.reportErrorCannotAssignRequiredComponent(descriptor, requirement, e2);
        }
        return retValue;
    }
    
    protected PropertyDescriptor findMatchingPropertyDescriptor(final ComponentRequirement requirement, final PropertyDescriptor[] propertyDescriptors) {
        PropertyDescriptor retValue = null;
        final String property = requirement.getFieldName();
        if (property != null) {
            retValue = this.getPropertyDescriptorByName(property, propertyDescriptors);
        }
        else {
            final String role = requirement.getRole();
            retValue = this.getPropertyDescriptorByType(role, propertyDescriptors);
        }
        return retValue;
    }
    
    protected PropertyDescriptor getPropertyDescriptorByName(final String name, final PropertyDescriptor[] propertyDescriptors) {
        PropertyDescriptor retValue = null;
        for (int i = 0; i < propertyDescriptors.length; ++i) {
            final PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
            if (name.equals(propertyDescriptor.getName())) {
                retValue = propertyDescriptor;
                break;
            }
        }
        return retValue;
    }
    
    protected PropertyDescriptor getPropertyDescriptorByType(final String type, final PropertyDescriptor[] propertyDescriptors) {
        PropertyDescriptor retValue = null;
        for (int i = 0; i < propertyDescriptors.length; ++i) {
            final PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
            if (propertyDescriptor.getPropertyType().toString().indexOf(type) > 0) {
                retValue = propertyDescriptor;
                break;
            }
        }
        return retValue;
    }
    
    private void reportErrorNoSuchProperty(final ComponentDescriptor descriptor, final ComponentRequirement requirement) throws CompositionException {
        final String causeDescriprion = "Failed to assign requirment using Java Bean introspection mechanism. No matching property was found in bean class";
        final String msg = this.getErrorMessage(descriptor, requirement, "Failed to assign requirment using Java Bean introspection mechanism. No matching property was found in bean class");
        throw new CompositionException(msg);
    }
    
    private void reportErrorCannotAssignRequiredComponent(final ComponentDescriptor descriptor, final ComponentRequirement requirement, final Exception e) throws CompositionException {
        final String causeDescriprion = "Failed to assign requirment using Java Bean introspection mechanism. ";
        final String msg = this.getErrorMessage(descriptor, requirement, "Failed to assign requirment using Java Bean introspection mechanism. ");
        throw new CompositionException(msg);
    }
    
    private void reportErrorCannotLookupRequiredComponent(final ComponentDescriptor descriptor, final ComponentRequirement requirement, final Throwable cause) throws CompositionException {
        final String causeDescriprion = "Failed to lookup required component.";
        final String msg = this.getErrorMessage(descriptor, requirement, "Failed to lookup required component.");
        throw new CompositionException(msg, cause);
    }
    
    private void reportErrorFailedToIntrospect(final ComponentDescriptor descriptor) throws CompositionException {
        final String msg = this.getErrorMessage(descriptor, null, null);
        throw new CompositionException(msg);
    }
    
    private String getErrorMessage(final ComponentDescriptor descriptor, final ComponentRequirement requirement, final String causeDescription) {
        final StringBuffer msg = new StringBuffer("Component composition failed.");
        msg.append("  Failed to resolve requirement for component of role: '");
        msg.append(descriptor.getRole());
        msg.append("'");
        if (descriptor.getRoleHint() != null) {
            msg.append(" and role-hint: '");
            msg.append(descriptor.getRoleHint());
            msg.append("'. ");
        }
        if (requirement != null) {
            msg.append("Failing requirement: " + requirement.getHumanReadableKey());
        }
        if (causeDescription != null) {
            msg.append(causeDescription);
        }
        return msg.toString();
    }
}
