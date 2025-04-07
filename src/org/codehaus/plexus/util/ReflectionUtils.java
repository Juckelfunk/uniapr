// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.lang.reflect.Field;

public final class ReflectionUtils
{
    public static Field getFieldByNameIncludingSuperclasses(final String fieldName, final Class clazz) {
        Field retValue = null;
        try {
            retValue = clazz.getDeclaredField(fieldName);
        }
        catch (NoSuchFieldException e) {
            final Class superclass = clazz.getSuperclass();
            if (superclass != null) {
                retValue = getFieldByNameIncludingSuperclasses(fieldName, superclass);
            }
        }
        return retValue;
    }
    
    public static List getFieldsIncludingSuperclasses(final Class clazz) {
        final List fields = new ArrayList(Arrays.asList(clazz.getDeclaredFields()));
        final Class superclass = clazz.getSuperclass();
        if (superclass != null) {
            fields.addAll(getFieldsIncludingSuperclasses(superclass));
        }
        return fields;
    }
    
    public static Method getSetter(String fieldName, final Class clazz) {
        final Method[] methods = clazz.getMethods();
        fieldName = "set" + StringUtils.capitalizeFirstLetter(fieldName);
        for (int i = 0; i < methods.length; ++i) {
            final Method method = methods[i];
            if (method.getName().equals(fieldName) && isSetter(method)) {
                return method;
            }
        }
        return null;
    }
    
    public static List getSetters(final Class clazz) {
        final Method[] methods = clazz.getMethods();
        final List list = new ArrayList();
        for (int i = 0; i < methods.length; ++i) {
            final Method method = methods[i];
            if (isSetter(method)) {
                list.add(method);
            }
        }
        return list;
    }
    
    public static Class getSetterType(final Method method) {
        if (!isSetter(method)) {
            throw new RuntimeException("The method " + method.getDeclaringClass().getName() + "." + method.getName() + " is not a setter.");
        }
        return method.getParameterTypes()[0];
    }
    
    public static void setVariableValueInObject(final Object object, final String variable, final Object value) throws IllegalAccessException {
        final Field field = getFieldByNameIncludingSuperclasses(variable, object.getClass());
        field.setAccessible(true);
        field.set(object, value);
    }
    
    public static Object getValueIncludingSuperclasses(final String variable, final Object object) throws IllegalAccessException {
        final Field field = getFieldByNameIncludingSuperclasses(variable, object.getClass());
        field.setAccessible(true);
        return field.get(object);
    }
    
    public static Map getVariablesAndValuesIncludingSuperclasses(final Object object) throws IllegalAccessException {
        final HashMap map = new HashMap();
        gatherVariablesAndValuesIncludingSuperclasses(object, map);
        return map;
    }
    
    public static boolean isSetter(final Method method) {
        return method.getReturnType().equals(Void.TYPE) && !Modifier.isStatic(method.getModifiers()) && method.getParameterTypes().length == 1;
    }
    
    private static void gatherVariablesAndValuesIncludingSuperclasses(final Object object, final Map map) throws IllegalAccessException {
        final Class clazz = object.getClass();
        final Field[] fields = clazz.getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);
        for (int i = 0; i < fields.length; ++i) {
            final Field field = fields[i];
            map.put(field.getName(), field.get(object));
        }
        final Class superclass = clazz.getSuperclass();
        if (!Object.class.equals(superclass)) {
            gatherVariablesAndValuesIncludingSuperclasses(superclass, map);
        }
    }
}
