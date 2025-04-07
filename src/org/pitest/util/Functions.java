// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.util;

import org.pitest.functional.predicate.Predicate;
import org.pitest.functional.F;

public abstract class Functions
{
    public static F<String, String> classNameToJVMClassName() {
        return new F<String, String>() {
            @Override
            public String apply(final String a) {
                return a.replace(".", "/");
            }
        };
    }
    
    public static F<String, String> jvmClassToClassName() {
        return new F<String, String>() {
            @Override
            public String apply(final String a) {
                return a.replace("/", ".");
            }
        };
    }
    
    public static F<Class<?>, String> classToName() {
        return new F<Class<?>, String>() {
            @Override
            public String apply(final Class<?> clazz) {
                return clazz.getName();
            }
        };
    }
    
    public static Predicate<String> startsWith(final String filter) {
        return new Predicate<String>() {
            @Override
            public Boolean apply(final String a) {
                return a.startsWith(filter);
            }
        };
    }
    
    public static <T extends Enum<T>> F<String, T> stringToEnum(final Class<T> clazz) {
        return new F<String, T>() {
            @Override
            public T apply(final String name) {
                return Enum.valueOf(clazz, name);
            }
        };
    }
}
