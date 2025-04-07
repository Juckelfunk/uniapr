// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project.interpolation;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import org.codehaus.plexus.logging.Logger;
import java.util.LinkedList;
import java.util.WeakHashMap;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;
import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.InterpolationPostProcessor;
import org.codehaus.plexus.interpolation.ValueSource;
import java.util.List;
import java.security.PrivilegedAction;
import java.security.AccessController;
import org.apache.maven.project.ProjectBuilderConfiguration;
import java.io.File;
import org.apache.maven.model.Model;
import org.apache.maven.project.path.PathTranslator;
import java.lang.reflect.Field;
import java.util.Map;

public class StringSearchModelInterpolator extends AbstractStringBasedModelInterpolator
{
    private static final Map<Class<?>, Field[]> fieldsByClass;
    private static final Map<Class<?>, Boolean> fieldIsPrimitiveByClass;
    
    public StringSearchModelInterpolator() {
    }
    
    public StringSearchModelInterpolator(final PathTranslator pathTranslator) {
        super(pathTranslator);
    }
    
    @Override
    public Model interpolate(final Model model, final File projectDir, final ProjectBuilderConfiguration config, final boolean debugEnabled) throws ModelInterpolationException {
        this.interpolateObject(model, model, projectDir, config, debugEnabled);
        return model;
    }
    
    protected void interpolateObject(final Object obj, final Model model, final File projectDir, final ProjectBuilderConfiguration config, final boolean debugEnabled) throws ModelInterpolationException {
        try {
            final List<ValueSource> valueSources = this.createValueSources(model, projectDir, config);
            final List<InterpolationPostProcessor> postProcessors = this.createPostProcessors(model, projectDir, config);
            final InterpolateObjectAction action = new InterpolateObjectAction(obj, valueSources, postProcessors, debugEnabled, this, this.getLogger());
            final ModelInterpolationException error = AccessController.doPrivileged((PrivilegedAction<ModelInterpolationException>)action);
            if (error != null) {
                throw error;
            }
        }
        finally {
            this.getInterpolator().clearAnswers();
        }
    }
    
    @Override
    protected Interpolator createInterpolator() {
        final StringSearchInterpolator interpolator = new StringSearchInterpolator();
        interpolator.setCacheAnswers(true);
        return interpolator;
    }
    
    static {
        fieldsByClass = new WeakHashMap<Class<?>, Field[]>();
        fieldIsPrimitiveByClass = new WeakHashMap<Class<?>, Boolean>();
    }
    
    private static final class InterpolateObjectAction implements PrivilegedAction<ModelInterpolationException>
    {
        private final boolean debugEnabled;
        private final LinkedList<Object> interpolationTargets;
        private final StringSearchModelInterpolator modelInterpolator;
        private final Logger logger;
        private final List<ValueSource> valueSources;
        private final List<InterpolationPostProcessor> postProcessors;
        
        public InterpolateObjectAction(final Object target, final List<ValueSource> valueSources, final List<InterpolationPostProcessor> postProcessors, final boolean debugEnabled, final StringSearchModelInterpolator modelInterpolator, final Logger logger) {
            this.valueSources = valueSources;
            this.postProcessors = postProcessors;
            this.debugEnabled = debugEnabled;
            (this.interpolationTargets = new LinkedList<Object>()).add(target);
            this.modelInterpolator = modelInterpolator;
            this.logger = logger;
        }
        
        public ModelInterpolationException run() {
            while (!this.interpolationTargets.isEmpty()) {
                final Object obj = this.interpolationTargets.removeFirst();
                try {
                    this.traverseObjectWithParents(obj.getClass(), obj);
                }
                catch (ModelInterpolationException e) {
                    return e;
                }
            }
            return null;
        }
        
        private void traverseObjectWithParents(final Class<?> cls, final Object target) throws ModelInterpolationException {
            if (cls == null) {
                return;
            }
            if (cls.isArray()) {
                this.evaluateArray(target);
            }
            else if (this.isQualifiedForInterpolation(cls)) {
                Field[] fields = StringSearchModelInterpolator.fieldsByClass.get(cls);
                if (fields == null) {
                    fields = cls.getDeclaredFields();
                    StringSearchModelInterpolator.fieldsByClass.put(cls, fields);
                }
                for (int i = 0; i < fields.length; ++i) {
                    final Class<?> type = fields[i].getType();
                    if (this.isQualifiedForInterpolation(fields[i], type)) {
                        final boolean isAccessible = fields[i].isAccessible();
                        fields[i].setAccessible(true);
                        try {
                            if (String.class == type) {
                                final String value = (String)fields[i].get(target);
                                if (value != null) {
                                    final String interpolated = this.modelInterpolator.interpolateInternal(value, this.valueSources, this.postProcessors, this.debugEnabled);
                                    if (!interpolated.equals(value)) {
                                        fields[i].set(target, interpolated);
                                    }
                                }
                            }
                            else if (Collection.class.isAssignableFrom(type)) {
                                final Collection<Object> c = (Collection<Object>)fields[i].get(target);
                                if (c != null && !c.isEmpty()) {
                                    final List<Object> originalValues = new ArrayList<Object>(c);
                                    try {
                                        c.clear();
                                    }
                                    catch (UnsupportedOperationException e3) {
                                        if (this.debugEnabled && this.logger != null) {
                                            this.logger.debug("Skipping interpolation of field: " + fields[i] + " in: " + cls.getName() + "; it is an unmodifiable collection.");
                                        }
                                        continue;
                                    }
                                    for (final Object value2 : originalValues) {
                                        if (value2 != null) {
                                            if (String.class == value2.getClass()) {
                                                final String interpolated2 = this.modelInterpolator.interpolateInternal((String)value2, this.valueSources, this.postProcessors, this.debugEnabled);
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
                                                    this.evaluateArray(value2);
                                                }
                                                else {
                                                    this.interpolationTargets.add(value2);
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
                                final Map<Object, Object> m = (Map<Object, Object>)fields[i].get(target);
                                if (m != null && !m.isEmpty()) {
                                    for (final Map.Entry<Object, Object> entry : m.entrySet()) {
                                        final Object value2 = entry.getValue();
                                        if (value2 != null) {
                                            if (String.class == value2.getClass()) {
                                                final String interpolated2 = this.modelInterpolator.interpolateInternal((String)value2, this.valueSources, this.postProcessors, this.debugEnabled);
                                                if (interpolated2.equals(value2)) {
                                                    continue;
                                                }
                                                try {
                                                    entry.setValue(interpolated2);
                                                }
                                                catch (UnsupportedOperationException e4) {
                                                    if (!this.debugEnabled || this.logger == null) {
                                                        continue;
                                                    }
                                                    this.logger.debug("Skipping interpolation of field: " + fields[i] + " (key: " + entry.getKey() + ") in: " + cls.getName() + "; it is an unmodifiable collection.");
                                                }
                                            }
                                            else if (value2.getClass().isArray()) {
                                                this.evaluateArray(value2);
                                            }
                                            else {
                                                this.interpolationTargets.add(value2);
                                            }
                                        }
                                    }
                                }
                            }
                            else {
                                final Object value3 = fields[i].get(target);
                                if (value3 != null) {
                                    if (fields[i].getType().isArray()) {
                                        this.evaluateArray(value3);
                                    }
                                    else {
                                        this.interpolationTargets.add(value3);
                                    }
                                }
                            }
                        }
                        catch (IllegalArgumentException e) {
                            throw new ModelInterpolationException("Failed to interpolate field: " + fields[i] + " on class: " + cls.getName(), e);
                        }
                        catch (IllegalAccessException e2) {
                            throw new ModelInterpolationException("Failed to interpolate field: " + fields[i] + " on class: " + cls.getName(), e2);
                        }
                        finally {
                            fields[i].setAccessible(isAccessible);
                        }
                    }
                }
                this.traverseObjectWithParents(cls.getSuperclass(), target);
            }
        }
        
        private boolean isQualifiedForInterpolation(final Class<?> cls) {
            return !cls.getPackage().getName().startsWith("java");
        }
        
        private boolean isQualifiedForInterpolation(final Field field, final Class<?> fieldType) {
            if (!StringSearchModelInterpolator.fieldIsPrimitiveByClass.containsKey(fieldType)) {
                StringSearchModelInterpolator.fieldIsPrimitiveByClass.put(fieldType, fieldType.isPrimitive());
            }
            return !StringSearchModelInterpolator.fieldIsPrimitiveByClass.get(fieldType) && !"parent".equals(field.getName());
        }
        
        private void evaluateArray(final Object target) throws ModelInterpolationException {
            for (int len = Array.getLength(target), i = 0; i < len; ++i) {
                final Object value = Array.get(target, i);
                if (value != null) {
                    if (String.class == value.getClass()) {
                        final String interpolated = this.modelInterpolator.interpolateInternal((String)value, this.valueSources, this.postProcessors, this.debugEnabled);
                        if (!interpolated.equals(value)) {
                            Array.set(target, i, interpolated);
                        }
                    }
                    else {
                        this.interpolationTargets.add(value);
                    }
                }
            }
        }
    }
}
