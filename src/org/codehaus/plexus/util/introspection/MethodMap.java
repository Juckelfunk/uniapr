// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.introspection;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;

public class MethodMap
{
    private static final int MORE_SPECIFIC = 0;
    private static final int LESS_SPECIFIC = 1;
    private static final int INCOMPARABLE = 2;
    Map methodByNameMap;
    
    public MethodMap() {
        this.methodByNameMap = new Hashtable();
    }
    
    public void add(final Method method) {
        final String methodName = method.getName();
        List l = this.get(methodName);
        if (l == null) {
            l = new ArrayList();
            this.methodByNameMap.put(methodName, l);
        }
        l.add(method);
    }
    
    public List get(final String key) {
        return this.methodByNameMap.get(key);
    }
    
    public Method find(final String methodName, final Object[] args) throws AmbiguousException {
        final List methodList = this.get(methodName);
        if (methodList == null) {
            return null;
        }
        final int l = args.length;
        final Class[] classes = new Class[l];
        for (int i = 0; i < l; ++i) {
            final Object arg = args[i];
            classes[i] = ((arg == null) ? null : arg.getClass());
        }
        return getMostSpecific(methodList, classes);
    }
    
    private static Method getMostSpecific(final List methods, final Class[] classes) throws AmbiguousException {
        final LinkedList applicables = getApplicables(methods, classes);
        if (applicables.isEmpty()) {
            return null;
        }
        if (applicables.size() == 1) {
            return applicables.getFirst();
        }
        final LinkedList maximals = new LinkedList();
        for (final Method app : applicables) {
            final Class[] appArgs = app.getParameterTypes();
            boolean lessSpecific = false;
            final Iterator maximal = maximals.iterator();
            while (!lessSpecific && maximal.hasNext()) {
                final Method max = maximal.next();
                switch (moreSpecific(appArgs, max.getParameterTypes())) {
                    case 0: {
                        maximal.remove();
                        continue;
                    }
                    case 1: {
                        lessSpecific = true;
                        continue;
                    }
                }
            }
            if (!lessSpecific) {
                maximals.addLast(app);
            }
        }
        if (maximals.size() > 1) {
            throw new AmbiguousException();
        }
        return maximals.getFirst();
    }
    
    private static int moreSpecific(final Class[] c1, final Class[] c2) {
        boolean c1MoreSpecific = false;
        boolean c2MoreSpecific = false;
        for (int i = 0; i < c1.length; ++i) {
            if (c1[i] != c2[i]) {
                c1MoreSpecific = (c1MoreSpecific || isStrictMethodInvocationConvertible(c2[i], c1[i]));
                c2MoreSpecific = (c2MoreSpecific || isStrictMethodInvocationConvertible(c1[i], c2[i]));
            }
        }
        if (c1MoreSpecific) {
            if (c2MoreSpecific) {
                return 2;
            }
            return 0;
        }
        else {
            if (c2MoreSpecific) {
                return 1;
            }
            return 2;
        }
    }
    
    private static LinkedList getApplicables(final List methods, final Class[] classes) {
        final LinkedList list = new LinkedList();
        for (final Method method : methods) {
            if (isApplicable(method, classes)) {
                list.add(method);
            }
        }
        return list;
    }
    
    private static boolean isApplicable(final Method method, final Class[] classes) {
        final Class[] methodArgs = method.getParameterTypes();
        if (methodArgs.length != classes.length) {
            return false;
        }
        for (int i = 0; i < classes.length; ++i) {
            if (!isMethodInvocationConvertible(methodArgs[i], classes[i])) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean isMethodInvocationConvertible(final Class formal, final Class actual) {
        if (actual == null && !formal.isPrimitive()) {
            return true;
        }
        if (actual != null && formal.isAssignableFrom(actual)) {
            return true;
        }
        if (formal.isPrimitive()) {
            if (formal == Boolean.TYPE && actual == Boolean.class) {
                return true;
            }
            if (formal == Character.TYPE && actual == Character.class) {
                return true;
            }
            if (formal == Byte.TYPE && actual == Byte.class) {
                return true;
            }
            if (formal == Short.TYPE && (actual == Short.class || actual == Byte.class)) {
                return true;
            }
            if (formal == Integer.TYPE && (actual == Integer.class || actual == Short.class || actual == Byte.class)) {
                return true;
            }
            if (formal == Long.TYPE && (actual == Long.class || actual == Integer.class || actual == Short.class || actual == Byte.class)) {
                return true;
            }
            if (formal == Float.TYPE && (actual == Float.class || actual == Long.class || actual == Integer.class || actual == Short.class || actual == Byte.class)) {
                return true;
            }
            if (formal == Double.TYPE && (actual == Double.class || actual == Float.class || actual == Long.class || actual == Integer.class || actual == Short.class || actual == Byte.class)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean isStrictMethodInvocationConvertible(final Class formal, final Class actual) {
        if (actual == null && !formal.isPrimitive()) {
            return true;
        }
        if (formal.isAssignableFrom(actual)) {
            return true;
        }
        if (formal.isPrimitive()) {
            if (formal == Short.TYPE && actual == Byte.TYPE) {
                return true;
            }
            if (formal == Integer.TYPE && (actual == Short.TYPE || actual == Byte.TYPE)) {
                return true;
            }
            if (formal == Long.TYPE && (actual == Integer.TYPE || actual == Short.TYPE || actual == Byte.TYPE)) {
                return true;
            }
            if (formal == Float.TYPE && (actual == Long.TYPE || actual == Integer.TYPE || actual == Short.TYPE || actual == Byte.TYPE)) {
                return true;
            }
            if (formal == Double.TYPE && (actual == Float.TYPE || actual == Long.TYPE || actual == Integer.TYPE || actual == Short.TYPE || actual == Byte.TYPE)) {
                return true;
            }
        }
        return false;
    }
    
    public static class AmbiguousException extends Exception
    {
    }
}
