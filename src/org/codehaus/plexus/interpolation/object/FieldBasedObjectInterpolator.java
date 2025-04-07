// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.interpolation.object;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.Collections;
import java.util.HashSet;
import java.util.WeakHashMap;
import java.security.PrivilegedAction;
import java.security.AccessController;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.RecursionInterceptor;
import org.codehaus.plexus.interpolation.SimpleRecursionInterceptor;
import org.codehaus.plexus.interpolation.Interpolator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FieldBasedObjectInterpolator implements ObjectInterpolator
{
    public static final Set DEFAULT_BLACKLISTED_FIELD_NAMES;
    public static final Set DEFAULT_BLACKLISTED_PACKAGE_PREFIXES;
    private static final Map fieldsByClass;
    private static final Map fieldIsPrimitiveByClass;
    private Set blacklistedFieldNames;
    private Set blacklistedPackagePrefixes;
    private List warnings;
    
    public static void clearCaches() {
        FieldBasedObjectInterpolator.fieldsByClass.clear();
        FieldBasedObjectInterpolator.fieldIsPrimitiveByClass.clear();
    }
    
    public FieldBasedObjectInterpolator() {
        this.warnings = new ArrayList();
        this.blacklistedFieldNames = FieldBasedObjectInterpolator.DEFAULT_BLACKLISTED_FIELD_NAMES;
        this.blacklistedPackagePrefixes = FieldBasedObjectInterpolator.DEFAULT_BLACKLISTED_PACKAGE_PREFIXES;
    }
    
    public FieldBasedObjectInterpolator(final Set blacklistedFieldNames, final Set blacklistedPackagePrefixes) {
        this.warnings = new ArrayList();
        this.blacklistedFieldNames = blacklistedFieldNames;
        this.blacklistedPackagePrefixes = blacklistedPackagePrefixes;
    }
    
    public boolean hasWarnings() {
        return this.warnings != null && !this.warnings.isEmpty();
    }
    
    public List getWarnings() {
        return new ArrayList(this.warnings);
    }
    
    public void interpolate(final Object target, final Interpolator interpolator) throws InterpolationException {
        this.interpolate(target, interpolator, new SimpleRecursionInterceptor());
    }
    
    public void interpolate(final Object target, final Interpolator interpolator, final RecursionInterceptor recursionInterceptor) throws InterpolationException {
        this.warnings.clear();
        final InterpolateObjectAction action = new InterpolateObjectAction(target, interpolator, recursionInterceptor, this.blacklistedFieldNames, this.blacklistedPackagePrefixes, this.warnings);
        final InterpolationException error = AccessController.doPrivileged((PrivilegedAction<InterpolationException>)action);
        if (error != null) {
            throw error;
        }
    }
    
    static {
        fieldsByClass = new WeakHashMap();
        fieldIsPrimitiveByClass = new WeakHashMap();
        final Set blacklistedFields = new HashSet();
        blacklistedFields.add("parent");
        DEFAULT_BLACKLISTED_FIELD_NAMES = Collections.unmodifiableSet((Set<?>)blacklistedFields);
        final Set blacklistedPackages = new HashSet();
        blacklistedPackages.add("java");
        DEFAULT_BLACKLISTED_PACKAGE_PREFIXES = Collections.unmodifiableSet((Set<?>)blacklistedPackages);
    }
    
    private static final class InterpolateObjectAction implements PrivilegedAction
    {
        private final LinkedList interpolationTargets;
        private final Interpolator interpolator;
        private final Set blacklistedFieldNames;
        private final String[] blacklistedPackagePrefixes;
        private final List warningCollector;
        private final RecursionInterceptor recursionInterceptor;
        
        public InterpolateObjectAction(final Object target, final Interpolator interpolator, final RecursionInterceptor recursionInterceptor, final Set blacklistedFieldNames, final Set blacklistedPackagePrefixes, final List warningCollector) {
            this.recursionInterceptor = recursionInterceptor;
            this.blacklistedFieldNames = blacklistedFieldNames;
            this.warningCollector = warningCollector;
            this.blacklistedPackagePrefixes = blacklistedPackagePrefixes.toArray(new String[0]);
            (this.interpolationTargets = new LinkedList()).add(new InterpolationTarget(target, ""));
            this.interpolator = interpolator;
        }
        
        public Object run() {
            while (!this.interpolationTargets.isEmpty()) {
                final InterpolationTarget target = this.interpolationTargets.removeFirst();
                try {
                    this.traverseObjectWithParents(target.value.getClass(), target);
                }
                catch (InterpolationException e) {
                    return e;
                }
            }
            return null;
        }
        
        private void traverseObjectWithParents(final Class cls, final InterpolationTarget target) throws InterpolationException {
            final Object obj = target.value;
            final String basePath = target.path;
            if (cls == null) {
                return;
            }
            if (cls.isArray()) {
                this.evaluateArray(obj, basePath);
            }
            else if (this.isQualifiedForInterpolation(cls)) {
                Field[] fields = FieldBasedObjectInterpolator.fieldsByClass.get(cls);
                if (fields == null) {
                    fields = cls.getDeclaredFields();
                    FieldBasedObjectInterpolator.fieldsByClass.put(cls, fields);
                }
                for (int i = 0; i < fields.length; ++i) {
                    final Class type = fields[i].getType();
                    if (this.isQualifiedForInterpolation(fields[i], type)) {
                        final boolean isAccessible = fields[i].isAccessible();
                        fields[i].setAccessible(true);
                        try {
                            if (String.class == type) {
                                final String value = (String)fields[i].get(obj);
                                if (value != null) {
                                    final String interpolated = this.interpolator.interpolate(value, this.recursionInterceptor);
                                    if (!interpolated.equals(value)) {
                                        fields[i].set(obj, interpolated);
                                    }
                                }
                            }
                            else if (Collection.class.isAssignableFrom(type)) {
                                final Collection c = (Collection)fields[i].get(obj);
                                if (c != null && !c.isEmpty()) {
                                    final List originalValues = new ArrayList(c);
                                    try {
                                        c.clear();
                                    }
                                    catch (UnsupportedOperationException e) {
                                        this.warningCollector.add(new ObjectInterpolationWarning("Field is an unmodifiable collection. Skipping interpolation.", basePath + "." + fields[i].getName(), e));
                                        continue;
                                    }
                                    for (final Object value2 : originalValues) {
                                        if (value2 != null) {
                                            if (String.class == value2.getClass()) {
                                                final String interpolated2 = this.interpolator.interpolate((String)value2, this.recursionInterceptor);
                                                if (!interpolated2.equals(value2)) {
                                                    c.add(interpolated2);
                                                }
                                                else {
                                                    c.add(value2);
                                                }
                                            }
                                            else {
                                                c.add(value2);
                                                if (value2.getClass().isArray()) {
                                                    this.evaluateArray(value2, basePath + "." + fields[i].getName());
                                                }
                                                else {
                                                    this.interpolationTargets.add(new InterpolationTarget(value2, basePath + "." + fields[i].getName()));
                                                }
                                            }
                                        }
                                        else {
                                            c.add(value2);
                                        }
                                    }
                                }
                            }
                            else if (Map.class.isAssignableFrom(type)) {
                                final Map m = (Map)fields[i].get(obj);
                                if (m != null && !m.isEmpty()) {
                                    for (final Map.Entry entry : m.entrySet()) {
                                        final Object value2 = entry.getValue();
                                        if (value2 != null) {
                                            if (String.class == value2.getClass()) {
                                                final String interpolated2 = this.interpolator.interpolate((String)value2, this.recursionInterceptor);
                                                if (interpolated2.equals(value2)) {
                                                    continue;
                                                }
                                                try {
                                                    entry.setValue(interpolated2);
                                                }
                                                catch (UnsupportedOperationException e2) {
                                                    this.warningCollector.add(new ObjectInterpolationWarning("Field is an unmodifiable collection. Skipping interpolation.", basePath + "." + fields[i].getName(), e2));
                                                }
                                            }
                                            else if (value2.getClass().isArray()) {
                                                this.evaluateArray(value2, basePath + "." + fields[i].getName());
                                            }
                                            else {
                                                this.interpolationTargets.add(new InterpolationTarget(value2, basePath + "." + fields[i].getName()));
                                            }
                                        }
                                    }
                                }
                            }
                            else {
                                final Object value3 = fields[i].get(obj);
                                if (value3 != null) {
                                    if (fields[i].getType().isArray()) {
                                        this.evaluateArray(value3, basePath + "." + fields[i].getName());
                                    }
                                    else {
                                        this.interpolationTargets.add(new InterpolationTarget(value3, basePath + "." + fields[i].getName()));
                                    }
                                }
                            }
                        }
                        catch (IllegalArgumentException e3) {
                            this.warningCollector.add(new ObjectInterpolationWarning("Failed to interpolate field. Skipping.", basePath + "." + fields[i].getName(), e3));
                        }
                        catch (IllegalAccessException e4) {
                            this.warningCollector.add(new ObjectInterpolationWarning("Failed to interpolate field. Skipping.", basePath + "." + fields[i].getName(), e4));
                        }
                        finally {
                            fields[i].setAccessible(isAccessible);
                        }
                    }
                }
                this.traverseObjectWithParents(cls.getSuperclass(), target);
            }
        }
        
        private boolean isQualifiedForInterpolation(final Class cls) {
            final String pkgName = cls.getPackage().getName();
            for (int i = 0; i < this.blacklistedPackagePrefixes.length; ++i) {
                final String prefix = this.blacklistedPackagePrefixes[i];
                if (pkgName.startsWith(prefix)) {
                    return false;
                }
            }
            return true;
        }
        
        private boolean isQualifiedForInterpolation(final Field field, final Class fieldType) {
            if (!FieldBasedObjectInterpolator.fieldIsPrimitiveByClass.containsKey(fieldType)) {
                FieldBasedObjectInterpolator.fieldIsPrimitiveByClass.put(fieldType, fieldType.isPrimitive());
            }
            return !FieldBasedObjectInterpolator.fieldIsPrimitiveByClass.get(fieldType) && !this.blacklistedFieldNames.contains(field.getName());
        }
        
        private void evaluateArray(final Object target, final String basePath) throws InterpolationException {
            for (int len = Array.getLength(target), i = 0; i < len; ++i) {
                final Object value = Array.get(target, i);
                if (value != null) {
                    if (String.class == value.getClass()) {
                        final String interpolated = this.interpolator.interpolate((String)value, this.recursionInterceptor);
                        if (!interpolated.equals(value)) {
                            Array.set(target, i, interpolated);
                        }
                    }
                    else {
                        this.interpolationTargets.add(new InterpolationTarget(value, basePath + "[" + i + "]"));
                    }
                }
            }
        }
    }
    
    private static final class InterpolationTarget
    {
        private Object value;
        private String path;
        
        private InterpolationTarget(final Object value, final String path) {
            this.value = value;
            this.path = path;
        }
    }
}
