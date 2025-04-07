// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.reflection;

import java.util.List;
import java.util.Arrays;
import java.lang.reflect.Field;
import java.util.Collection;
import org.pitest.functional.F;
import org.pitest.functional.FArray;
import java.util.LinkedHashSet;
import java.util.Set;
import java.lang.reflect.Method;
import org.pitest.functional.predicate.Predicate;

public abstract class Reflection
{
    public static Method publicMethod(final Class<?> clazz, final Predicate<Method> p) {
        return publicMethods(clazz, p).iterator().next();
    }
    
    public static Set<Method> publicMethods(final Class<?> clazz, final Predicate<Method> p) {
        final Set<Method> ms = new LinkedHashSet<Method>();
        FArray.filter(clazz.getMethods(), p, ms);
        return ms;
    }
    
    public static Set<Field> publicFields(final Class<?> clazz) {
        final Set<Field> fields = new LinkedHashSet<Field>();
        if (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getFields()));
        }
        return fields;
    }
    
    public static Set<Method> allMethods(final Class<?> c) {
        final Set<Method> methods = new LinkedHashSet<Method>();
        if (c != null) {
            final List<Method> locallyDeclaredMethods = Arrays.asList(c.getDeclaredMethods());
            methods.addAll(locallyDeclaredMethods);
            methods.addAll(allMethods(c.getSuperclass()));
        }
        return methods;
    }
    
    public static Method publicMethod(final Class<?> clazz, final String name) {
        final Predicate<Method> p = new Predicate<Method>() {
            @Override
            public Boolean apply(final Method a) {
                return a.getName().equals(name);
            }
        };
        return publicMethod(clazz, p);
    }
}
