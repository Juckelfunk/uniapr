// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.classinfo;

import org.pitest.util.Log;
import org.pitest.util.IsolationUtils;
import org.pitest.functional.Option;
import org.pitest.functional.F;
import java.util.logging.Logger;
import java.io.Serializable;

public final class ClassName implements Comparable<ClassName>, Serializable
{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG;
    private static final ClassName OBJECT;
    private static final ClassName STRING;
    private final String name;
    
    private ClassName(final String name) {
        this.name = name;
    }
    
    public static ClassName fromClass(final Class<?> clazz) {
        return fromString(clazz.getName());
    }
    
    public static ClassName fromString(final String clazz) {
        final String name = clazz.replace('.', '/');
        if (name.equals(ClassName.OBJECT.asInternalName())) {
            return ClassName.OBJECT;
        }
        if (name.equals(ClassName.STRING.asInternalName())) {
            return ClassName.STRING;
        }
        return new ClassName(name);
    }
    
    public String asJavaName() {
        return this.name.replace('/', '.');
    }
    
    public String asInternalName() {
        return this.name;
    }
    
    public ClassName getNameWithoutPackage() {
        final int lastSeparator = this.name.lastIndexOf(47);
        if (lastSeparator != -1) {
            return fromString(this.name.substring(lastSeparator + 1, this.name.length()));
        }
        return this;
    }
    
    public ClassName getPackage() {
        final int lastSeparator = this.name.lastIndexOf(47);
        if (lastSeparator != -1) {
            return fromString(this.name.substring(0, lastSeparator));
        }
        return fromString("");
    }
    
    public ClassName withoutPrefixChars(final int prefixLength) {
        final String nameWithoutPackage = this.getNameWithoutPackage().asJavaName();
        return fromString(this.getPackage().asJavaName() + "/" + nameWithoutPackage.substring(prefixLength, nameWithoutPackage.length()));
    }
    
    public ClassName withoutSuffixChars(final int suffixLength) {
        final String nameWithoutPacakge = this.getNameWithoutPackage().asJavaName();
        return fromString(this.getPackage().asJavaName() + "/" + nameWithoutPacakge.substring(0, nameWithoutPacakge.length() - suffixLength));
    }
    
    public static F<String, ClassName> stringToClassName() {
        return new F<String, ClassName>() {
            @Override
            public ClassName apply(final String clazz) {
                return ClassName.fromString(clazz);
            }
        };
    }
    
    public static F<ClassName, Option<Class<?>>> nameToClass() {
        return nameToClass(IsolationUtils.getContextClassLoader());
    }
    
    public static F<ClassName, Option<Class<?>>> nameToClass(final ClassLoader loader) {
        return new F<ClassName, Option<Class<?>>>() {
            @Override
            public Option<Class<?>> apply(final ClassName className) {
                try {
                    final Class<?> clazz = Class.forName(className.asJavaName(), false, loader);
                    return Option.some(clazz);
                }
                catch (ClassNotFoundException e) {
                    ClassName.LOG.warning("Could not load " + className + " (ClassNotFoundException: " + e.getMessage() + ")");
                    return (Option<Class<?>>)Option.none();
                }
                catch (NoClassDefFoundError e2) {
                    ClassName.LOG.warning("Could not load " + className + " (NoClassDefFoundError: " + e2.getMessage() + ")");
                    return (Option<Class<?>>)Option.none();
                }
                catch (LinkageError e3) {
                    ClassName.LOG.warning("Could not load " + className + " " + e3.getMessage());
                    return (Option<Class<?>>)Option.none();
                }
                catch (SecurityException e4) {
                    ClassName.LOG.warning("Could not load " + className + " " + e4.getMessage());
                    return (Option<Class<?>>)Option.none();
                }
            }
        };
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.name == null) ? 0 : this.name.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final ClassName other = (ClassName)obj;
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        }
        else if (!this.name.equals(other.name)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return this.asJavaName();
    }
    
    @Override
    public int compareTo(final ClassName o) {
        return this.asJavaName().compareTo(o.asJavaName());
    }
    
    static {
        LOG = Log.getLogger();
        OBJECT = new ClassName("java/lang/Object");
        STRING = new ClassName("java/lang/String");
    }
}
