// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.composition;

import java.util.Set;
import org.codehaus.plexus.util.ReflectionUtils;
import java.util.Map;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import java.util.ArrayList;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Collection;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import java.util.LinkedList;
import java.util.List;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

public class FieldComponentComposer extends AbstractComponentComposer
{
    public List assembleComponent(final Object component, final ComponentDescriptor componentDescriptor, final PlexusContainer container) throws CompositionException {
        final List retValue = new LinkedList();
        final List requirements = componentDescriptor.getRequirements();
        for (final ComponentRequirement requirement : requirements) {
            final Field field = this.findMatchingField(component, componentDescriptor, requirement, container);
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            final List descriptors = this.assignRequirementToField(component, field, container, requirement);
            retValue.addAll(descriptors);
        }
        return retValue;
    }
    
    private List assignRequirementToField(final Object component, final Field field, final PlexusContainer container, final ComponentRequirement requirement) throws CompositionException {
        try {
            final String role = requirement.getRole();
            List retValue;
            if (field.getType().isArray()) {
                final List dependencies = container.lookupList(role);
                final Object[] array = (Object[])Array.newInstance(field.getType(), dependencies.size());
                retValue = container.getComponentDescriptorList(role);
                field.set(component, dependencies.toArray(array));
            }
            else if (Map.class.isAssignableFrom(field.getType())) {
                final Map dependencies2 = container.lookupMap(role);
                retValue = container.getComponentDescriptorList(role);
                field.set(component, dependencies2);
            }
            else if (List.class.isAssignableFrom(field.getType())) {
                final List dependencies = container.lookupList(role);
                retValue = container.getComponentDescriptorList(role);
                field.set(component, dependencies);
            }
            else if (Set.class.isAssignableFrom(field.getType())) {
                final Map dependencies2 = container.lookupMap(role);
                retValue = container.getComponentDescriptorList(role);
                field.set(component, dependencies2.entrySet());
            }
            else {
                final String key = requirement.getRequirementKey();
                final Object dependency = container.lookup(key);
                final ComponentDescriptor componentDescriptor = container.getComponentDescriptor(key);
                retValue = new ArrayList(1);
                retValue.add(componentDescriptor);
                field.set(component, dependency);
            }
            return retValue;
        }
        catch (IllegalArgumentException e) {
            throw new CompositionException("Composition failed for the field " + field.getName() + " " + "in object of type " + component.getClass().getName(), e);
        }
        catch (IllegalAccessException e2) {
            throw new CompositionException("Composition failed for the field " + field.getName() + " " + "in object of type " + component.getClass().getName(), e2);
        }
        catch (ComponentLookupException e3) {
            throw new CompositionException("Composition failed of field " + field.getName() + " " + "in object of type " + component.getClass().getName() + " because the requirement " + requirement + " was missing", e3);
        }
    }
    
    protected Field findMatchingField(final Object component, final ComponentDescriptor componentDescriptor, final ComponentRequirement requirement, final PlexusContainer container) throws CompositionException {
        final String fieldName = requirement.getFieldName();
        Field field = null;
        if (fieldName != null) {
            field = this.getFieldByName(component, fieldName, componentDescriptor);
        }
        else {
            Class fieldClass = null;
            try {
                if (container != null) {
                    fieldClass = container.getContainerRealm().loadClass(requirement.getRole());
                }
                else {
                    fieldClass = Thread.currentThread().getContextClassLoader().loadClass(requirement.getRole());
                }
            }
            catch (ClassNotFoundException e) {
                final StringBuffer msg = new StringBuffer("Component Composition failed for component: ");
                msg.append(componentDescriptor.getHumanReadableKey());
                msg.append(": Requirement class: '");
                msg.append(requirement.getRole());
                msg.append("' not found.");
                throw new CompositionException(msg.toString(), e);
            }
            field = this.getFieldByType(component, fieldClass, componentDescriptor);
        }
        return field;
    }
    
    protected Field getFieldByName(final Object component, final String fieldName, final ComponentDescriptor componentDescriptor) throws CompositionException {
        final Field field = ReflectionUtils.getFieldByNameIncludingSuperclasses(fieldName, component.getClass());
        if (field == null) {
            final StringBuffer msg = new StringBuffer("Component Composition failed. No field of name: '");
            msg.append(fieldName);
            msg.append("' exists in component: ");
            msg.append(componentDescriptor.getHumanReadableKey());
            throw new CompositionException(msg.toString());
        }
        return field;
    }
    
    protected Field getFieldByTypeIncludingSuperclasses(final Class componentClass, final Class type, final ComponentDescriptor componentDescriptor) throws CompositionException {
        final List fields = this.getFieldsByTypeIncludingSuperclasses(componentClass, type, componentDescriptor);
        if (fields.size() == 0) {
            return null;
        }
        if (fields.size() == 1) {
            return fields.get(0);
        }
        throw new CompositionException("There are several fields of type '" + type + "', " + "use 'field-name' to select the correct field.");
    }
    
    protected List getFieldsByTypeIncludingSuperclasses(final Class componentClass, final Class type, final ComponentDescriptor componentDescriptor) throws CompositionException {
        final Class arrayType = Array.newInstance(type, 0).getClass();
        final Field[] fields = componentClass.getDeclaredFields();
        final List foundFields = new ArrayList();
        for (int i = 0; i < fields.length; ++i) {
            final Class fieldType = fields[i].getType();
            if (fieldType.isAssignableFrom(type) || fieldType.isAssignableFrom(arrayType)) {
                foundFields.add(fields[i]);
            }
        }
        if (componentClass.getSuperclass() != Object.class) {
            final List superFields = this.getFieldsByTypeIncludingSuperclasses(componentClass.getSuperclass(), type, componentDescriptor);
            foundFields.addAll(superFields);
        }
        return foundFields;
    }
    
    protected Field getFieldByType(final Object component, final Class type, final ComponentDescriptor componentDescriptor) throws CompositionException {
        final Field field = this.getFieldByTypeIncludingSuperclasses(component.getClass(), type, componentDescriptor);
        if (field == null) {
            final StringBuffer msg = new StringBuffer("Component composition failed. No field of type: '");
            msg.append(type);
            msg.append("' exists in class '");
            msg.append(component.getClass().getName());
            msg.append("'.");
            if (componentDescriptor != null) {
                msg.append(" Component: ");
                msg.append(componentDescriptor.getHumanReadableKey());
            }
            throw new CompositionException(msg.toString());
        }
        return field;
    }
}
