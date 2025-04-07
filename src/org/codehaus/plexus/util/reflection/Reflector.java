// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.reflection;

import java.util.Map;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public final class Reflector
{
    private static final String CONSTRUCTOR_METHOD_NAME = "$$CONSTRUCTOR$$";
    private static final String GET_INSTANCE_METHOD_NAME = "getInstance";
    private HashMap classMaps;
    
    public Reflector() {
        this.classMaps = new HashMap();
    }
    
    public Object newInstance(final Class theClass, Object[] params) throws ReflectorException {
        if (params == null) {
            params = new Object[0];
        }
        final Class[] paramTypes = new Class[params.length];
        for (int i = 0, len = params.length; i < len; ++i) {
            paramTypes[i] = params[i].getClass();
        }
        try {
            final Constructor con = this.getConstructor(theClass, paramTypes);
            if (con == null) {
                final StringBuffer buffer = new StringBuffer();
                buffer.append("Constructor not found for class: ");
                buffer.append(theClass.getName());
                buffer.append(" with specified or ancestor parameter classes: ");
                for (int j = 0; j < paramTypes.length; ++j) {
                    buffer.append(paramTypes[j].getName());
                    buffer.append(',');
                }
                buffer.setLength(buffer.length() - 1);
                throw new ReflectorException(buffer.toString());
            }
            return con.newInstance(params);
        }
        catch (InstantiationException ex) {
            throw new ReflectorException(ex);
        }
        catch (InvocationTargetException ex2) {
            throw new ReflectorException(ex2);
        }
        catch (IllegalAccessException ex3) {
            throw new ReflectorException(ex3);
        }
    }
    
    public Object getSingleton(final Class theClass, final Object[] initParams) throws ReflectorException {
        final Class[] paramTypes = new Class[initParams.length];
        for (int i = 0, len = initParams.length; i < len; ++i) {
            paramTypes[i] = initParams[i].getClass();
        }
        try {
            final Method method = this.getMethod(theClass, "getInstance", paramTypes);
            return method.invoke(null, initParams);
        }
        catch (InvocationTargetException ex) {
            throw new ReflectorException(ex);
        }
        catch (IllegalAccessException ex2) {
            throw new ReflectorException(ex2);
        }
    }
    
    public Object invoke(final Object target, final String methodName, Object[] params) throws ReflectorException {
        if (params == null) {
            params = new Object[0];
        }
        final Class[] paramTypes = new Class[params.length];
        for (int i = 0, len = params.length; i < len; ++i) {
            paramTypes[i] = params[i].getClass();
        }
        try {
            final Method method = this.getMethod(target.getClass(), methodName, paramTypes);
            if (method == null) {
                final StringBuffer buffer = new StringBuffer();
                buffer.append("Singleton-producing method named '").append(methodName).append("' not found with specified parameter classes: ");
                for (int j = 0; j < paramTypes.length; ++j) {
                    buffer.append(paramTypes[j].getName());
                    buffer.append(',');
                }
                buffer.setLength(buffer.length() - 1);
                throw new ReflectorException(buffer.toString());
            }
            return method.invoke(target, params);
        }
        catch (InvocationTargetException ex) {
            throw new ReflectorException(ex);
        }
        catch (IllegalAccessException ex2) {
            throw new ReflectorException(ex2);
        }
    }
    
    public Object getStaticField(final Class targetClass, final String fieldName) throws ReflectorException {
        try {
            final Field field = targetClass.getField(fieldName);
            return field.get(null);
        }
        catch (SecurityException e) {
            throw new ReflectorException(e);
        }
        catch (NoSuchFieldException e2) {
            throw new ReflectorException(e2);
        }
        catch (IllegalArgumentException e3) {
            throw new ReflectorException(e3);
        }
        catch (IllegalAccessException e4) {
            throw new ReflectorException(e4);
        }
    }
    
    public Object getField(final Object target, final String fieldName) throws ReflectorException {
        return this.getField(target, fieldName, false);
    }
    
    public Object getField(final Object target, final String fieldName, final boolean breakAccessibility) throws ReflectorException {
        Class targetClass = target.getClass();
        while (targetClass != null) {
            try {
                final Field field = targetClass.getDeclaredField(fieldName);
                boolean accessibilityBroken = false;
                if (!field.isAccessible() && breakAccessibility) {
                    field.setAccessible(true);
                    accessibilityBroken = true;
                }
                final Object result = field.get(target);
                if (accessibilityBroken) {
                    field.setAccessible(false);
                }
                return result;
            }
            catch (SecurityException e) {
                throw new ReflectorException(e);
            }
            catch (NoSuchFieldException e2) {
                if (targetClass == Object.class) {
                    throw new ReflectorException(e2);
                }
                targetClass = targetClass.getSuperclass();
                continue;
            }
            catch (IllegalAccessException e3) {
                throw new ReflectorException(e3);
            }
            break;
        }
        return null;
    }
    
    public Object invokeStatic(final Class targetClass, final String methodName, Object[] params) throws ReflectorException {
        if (params == null) {
            params = new Object[0];
        }
        final Class[] paramTypes = new Class[params.length];
        for (int i = 0, len = params.length; i < len; ++i) {
            paramTypes[i] = params[i].getClass();
        }
        try {
            final Method method = this.getMethod(targetClass, methodName, paramTypes);
            if (method == null) {
                final StringBuffer buffer = new StringBuffer();
                buffer.append("Singleton-producing method named '" + methodName + "' not found with specified parameter classes: ");
                for (int j = 0; j < paramTypes.length; ++j) {
                    buffer.append(paramTypes[j].getName());
                    buffer.append(',');
                }
                buffer.setLength(buffer.length() - 1);
                throw new ReflectorException(buffer.toString());
            }
            return method.invoke(null, params);
        }
        catch (InvocationTargetException ex) {
            throw new ReflectorException(ex);
        }
        catch (IllegalAccessException ex2) {
            throw new ReflectorException(ex2);
        }
    }
    
    public Constructor getConstructor(final Class targetClass, final Class[] params) throws ReflectorException {
        final Map constructorMap = this.getConstructorMap(targetClass);
        final StringBuffer key = new StringBuffer(200);
        key.append("(");
        for (int i = 0, len = params.length; i < len; ++i) {
            key.append(params[i].getName());
            key.append(",");
        }
        if (params.length > 0) {
            key.setLength(key.length() - 1);
        }
        key.append(")");
        Constructor constructor = null;
        final String paramKey = key.toString();
        synchronized (paramKey.intern()) {
            constructor = constructorMap.get(paramKey);
            if (constructor == null) {
                final Constructor[] cands = targetClass.getConstructors();
                for (int j = 0, len2 = cands.length; j < len2; ++j) {
                    final Class[] types = cands[j].getParameterTypes();
                    if (params.length == types.length) {
                        for (int k = 0, len3 = params.length; k < len3; ++k) {
                            if (!types[k].isAssignableFrom(params[k])) {}
                        }
                        constructor = cands[j];
                        constructorMap.put(paramKey, constructor);
                    }
                }
            }
        }
        if (constructor == null) {
            throw new ReflectorException("Error retrieving constructor object for: " + targetClass.getName() + paramKey);
        }
        return constructor;
    }
    
    public Object getObjectProperty(final Object target, final String propertyName) throws ReflectorException {
        Object returnValue = null;
        if (propertyName == null || propertyName.trim().length() < 1) {
            throw new ReflectorException("Cannot retrieve value for empty property.");
        }
        String beanAccessor = "get" + Character.toUpperCase(propertyName.charAt(0));
        if (propertyName.trim().length() > 1) {
            beanAccessor += propertyName.substring(1).trim();
        }
        final Class targetClass = target.getClass();
        final Class[] emptyParams = new Class[0];
        Method method = this._getMethod(targetClass, beanAccessor, emptyParams);
        if (method == null) {
            method = this._getMethod(targetClass, propertyName, emptyParams);
        }
        if (method != null) {
            try {
                returnValue = method.invoke(target, new Object[0]);
            }
            catch (IllegalAccessException e) {
                throw new ReflectorException("Error retrieving property '" + propertyName + "' from '" + targetClass + "'", e);
            }
            catch (InvocationTargetException e2) {
                throw new ReflectorException("Error retrieving property '" + propertyName + "' from '" + targetClass + "'", e2);
            }
        }
        if (method != null) {
            try {
                returnValue = method.invoke(target, new Object[0]);
                return returnValue;
            }
            catch (IllegalAccessException e) {
                throw new ReflectorException("Error retrieving property '" + propertyName + "' from '" + targetClass + "'", e);
            }
            catch (InvocationTargetException e2) {
                throw new ReflectorException("Error retrieving property '" + propertyName + "' from '" + targetClass + "'", e2);
            }
        }
        returnValue = this.getField(target, propertyName, true);
        if (method == null && returnValue == null) {
            throw new ReflectorException("Neither method: '" + propertyName + "' nor bean accessor: '" + beanAccessor + "' can be found for class: '" + targetClass + "', and retrieval of field: '" + propertyName + "' returned null as value.");
        }
        return returnValue;
    }
    
    public Method getMethod(final Class targetClass, final String methodName, final Class[] params) throws ReflectorException {
        final Method method = this._getMethod(targetClass, methodName, params);
        if (method == null) {
            throw new ReflectorException("Method: '" + methodName + "' not found in class: '" + targetClass + "'");
        }
        return method;
    }
    
    private Method _getMethod(final Class targetClass, final String methodName, final Class[] params) throws ReflectorException {
        final Map methodMap = this.getMethodMap(targetClass, methodName);
        final StringBuffer key = new StringBuffer(200);
        key.append("(");
        for (int i = 0, len = params.length; i < len; ++i) {
            key.append(params[i].getName());
            key.append(",");
        }
        key.append(")");
        Method method = null;
        final String paramKey = key.toString();
        synchronized (paramKey.intern()) {
            method = methodMap.get(paramKey);
            if (method == null) {
                final Method[] cands = targetClass.getMethods();
                for (int j = 0, len2 = cands.length; j < len2; ++j) {
                    final String name = cands[j].getName();
                    if (methodName.equals(name)) {
                        final Class[] types = cands[j].getParameterTypes();
                        if (params.length == types.length) {
                            for (int k = 0, len3 = params.length; k < len3; ++k) {
                                if (!types[k].isAssignableFrom(params[k])) {}
                            }
                            method = cands[j];
                            methodMap.put(paramKey, method);
                        }
                    }
                }
            }
        }
        return method;
    }
    
    private Map getConstructorMap(final Class theClass) throws ReflectorException {
        return this.getMethodMap(theClass, "$$CONSTRUCTOR$$");
    }
    
    private Map getMethodMap(final Class theClass, final String methodName) throws ReflectorException {
        Map methodMap = null;
        if (theClass == null) {
            return null;
        }
        final String className = theClass.getName();
        synchronized (className.intern()) {
            Map classMethods = this.classMaps.get(className);
            if (classMethods == null) {
                classMethods = new HashMap();
                methodMap = new HashMap();
                classMethods.put(methodName, methodMap);
                this.classMaps.put(className, classMethods);
            }
            else {
                final String key = className + "::" + methodName;
                synchronized (key.intern()) {
                    methodMap = classMethods.get(methodName);
                    if (methodMap == null) {
                        methodMap = new HashMap();
                        classMethods.put(methodName, methodMap);
                    }
                }
            }
        }
        return methodMap;
    }
}
