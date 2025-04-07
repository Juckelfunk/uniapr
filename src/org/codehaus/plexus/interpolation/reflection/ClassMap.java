// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.interpolation.reflection;

import java.lang.reflect.Modifier;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;

public class ClassMap
{
    private static final CacheMiss CACHE_MISS;
    private static final Object OBJECT;
    private Class clazz;
    private Map methodCache;
    private MethodMap methodMap;
    
    public ClassMap(final Class clazz) {
        this.methodCache = new Hashtable();
        this.methodMap = new MethodMap();
        this.clazz = clazz;
        this.populateMethodCache();
    }
    
    Class getCachedClass() {
        return this.clazz;
    }
    
    public Method findMethod(final String name, final Object[] params) throws MethodMap.AmbiguousException {
        final String methodKey = makeMethodKey(name, params);
        Object cacheEntry = this.methodCache.get(methodKey);
        if (cacheEntry == ClassMap.CACHE_MISS) {
            return null;
        }
        if (cacheEntry == null) {
            try {
                cacheEntry = this.methodMap.find(name, params);
            }
            catch (MethodMap.AmbiguousException ae) {
                this.methodCache.put(methodKey, ClassMap.CACHE_MISS);
                throw ae;
            }
            if (cacheEntry == null) {
                this.methodCache.put(methodKey, ClassMap.CACHE_MISS);
            }
            else {
                this.methodCache.put(methodKey, cacheEntry);
            }
        }
        return (Method)cacheEntry;
    }
    
    private void populateMethodCache() {
        final Method[] methods = getAccessibleMethods(this.clazz);
        for (int i = 0; i < methods.length; ++i) {
            final Method method = methods[i];
            final Method publicMethod = getPublicMethod(method);
            if (publicMethod != null) {
                this.methodMap.add(publicMethod);
                this.methodCache.put(this.makeMethodKey(publicMethod), publicMethod);
            }
        }
    }
    
    private String makeMethodKey(final Method method) {
        final Class[] parameterTypes = method.getParameterTypes();
        final StringBuffer methodKey = new StringBuffer(method.getName());
        for (int j = 0; j < parameterTypes.length; ++j) {
            if (parameterTypes[j].isPrimitive()) {
                if (parameterTypes[j].equals(Boolean.TYPE)) {
                    methodKey.append("java.lang.Boolean");
                }
                else if (parameterTypes[j].equals(Byte.TYPE)) {
                    methodKey.append("java.lang.Byte");
                }
                else if (parameterTypes[j].equals(Character.TYPE)) {
                    methodKey.append("java.lang.Character");
                }
                else if (parameterTypes[j].equals(Double.TYPE)) {
                    methodKey.append("java.lang.Double");
                }
                else if (parameterTypes[j].equals(Float.TYPE)) {
                    methodKey.append("java.lang.Float");
                }
                else if (parameterTypes[j].equals(Integer.TYPE)) {
                    methodKey.append("java.lang.Integer");
                }
                else if (parameterTypes[j].equals(Long.TYPE)) {
                    methodKey.append("java.lang.Long");
                }
                else if (parameterTypes[j].equals(Short.TYPE)) {
                    methodKey.append("java.lang.Short");
                }
            }
            else {
                methodKey.append(parameterTypes[j].getName());
            }
        }
        return methodKey.toString();
    }
    
    private static String makeMethodKey(final String method, final Object[] params) {
        final StringBuffer methodKey = new StringBuffer().append(method);
        for (int j = 0; j < params.length; ++j) {
            Object arg = params[j];
            if (arg == null) {
                arg = ClassMap.OBJECT;
            }
            methodKey.append(arg.getClass().getName());
        }
        return methodKey.toString();
    }
    
    private static Method[] getAccessibleMethods(final Class clazz) {
        Method[] methods = clazz.getMethods();
        if (Modifier.isPublic(clazz.getModifiers())) {
            return methods;
        }
        final MethodInfo[] methodInfos = new MethodInfo[methods.length];
        int i = methods.length;
        while (i-- > 0) {
            methodInfos[i] = new MethodInfo(methods[i]);
        }
        final int upcastCount = getAccessibleMethods(clazz, methodInfos, 0);
        if (upcastCount < methods.length) {
            methods = new Method[upcastCount];
        }
        int j = 0;
        for (int k = 0; k < methodInfos.length; ++k) {
            final MethodInfo methodInfo = methodInfos[k];
            if (methodInfo.upcast) {
                methods[j++] = methodInfo.method;
            }
        }
        return methods;
    }
    
    private static int getAccessibleMethods(final Class clazz, final MethodInfo[] methodInfos, int upcastCount) {
        final int l = methodInfos.length;
        if (Modifier.isPublic(clazz.getModifiers())) {
            for (int i = 0; i < l && upcastCount < l; ++i) {
                try {
                    final MethodInfo methodInfo = methodInfos[i];
                    if (!methodInfo.upcast) {
                        methodInfo.tryUpcasting(clazz);
                        ++upcastCount;
                    }
                }
                catch (NoSuchMethodException ex) {}
            }
            if (upcastCount == l) {
                return upcastCount;
            }
        }
        final Class superclazz = clazz.getSuperclass();
        if (superclazz != null) {
            upcastCount = getAccessibleMethods(superclazz, methodInfos, upcastCount);
            if (upcastCount == l) {
                return upcastCount;
            }
        }
        final Class[] interfaces = clazz.getInterfaces();
        int j = interfaces.length;
        while (j-- > 0) {
            upcastCount = getAccessibleMethods(interfaces[j], methodInfos, upcastCount);
            if (upcastCount == l) {
                return upcastCount;
            }
        }
        return upcastCount;
    }
    
    public static Method getPublicMethod(final Method method) {
        final Class clazz = method.getDeclaringClass();
        if ((clazz.getModifiers() & 0x1) != 0x0) {
            return method;
        }
        return getPublicMethod(clazz, method.getName(), method.getParameterTypes());
    }
    
    private static Method getPublicMethod(final Class clazz, final String name, final Class[] paramTypes) {
        if ((clazz.getModifiers() & 0x1) != 0x0) {
            try {
                return clazz.getMethod(name, (Class[])paramTypes);
            }
            catch (NoSuchMethodException e) {
                return null;
            }
        }
        final Class superclazz = clazz.getSuperclass();
        if (superclazz != null) {
            final Method superclazzMethod = getPublicMethod(superclazz, name, paramTypes);
            if (superclazzMethod != null) {
                return superclazzMethod;
            }
        }
        final Class[] interfaces = clazz.getInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            final Method interfaceMethod = getPublicMethod(interfaces[i], name, paramTypes);
            if (interfaceMethod != null) {
                return interfaceMethod;
            }
        }
        return null;
    }
    
    static {
        CACHE_MISS = new CacheMiss();
        OBJECT = new Object();
    }
    
    private static final class CacheMiss
    {
    }
    
    private static final class MethodInfo
    {
        Method method;
        String name;
        Class[] parameterTypes;
        boolean upcast;
        
        MethodInfo(final Method method) {
            this.method = null;
            this.name = method.getName();
            this.parameterTypes = method.getParameterTypes();
            this.upcast = false;
        }
        
        void tryUpcasting(final Class clazz) throws NoSuchMethodException {
            this.method = clazz.getMethod(this.name, (Class[])this.parameterTypes);
            this.name = null;
            this.parameterTypes = null;
            this.upcast = true;
        }
    }
}
