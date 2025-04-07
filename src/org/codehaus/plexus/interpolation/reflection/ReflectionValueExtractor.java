// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.interpolation.reflection;

import java.util.WeakHashMap;
import java.lang.reflect.Method;
import org.codehaus.plexus.interpolation.util.StringUtils;
import java.util.StringTokenizer;
import java.util.Map;

public class ReflectionValueExtractor
{
    private static final Class[] CLASS_ARGS;
    private static final Object[] OBJECT_ARGS;
    private static final Map classMaps;
    
    private ReflectionValueExtractor() {
    }
    
    public static Object evaluate(final String expression, final Object root) throws Exception {
        return evaluate(expression, root, true);
    }
    
    public static Object evaluate(String expression, final Object root, final boolean trimRootToken) throws Exception {
        if (trimRootToken) {
            expression = expression.substring(expression.indexOf(46) + 1);
        }
        Object value = root;
        final StringTokenizer parser = new StringTokenizer(expression, ".");
        while (parser.hasMoreTokens()) {
            final String token = parser.nextToken();
            if (value == null) {
                return null;
            }
            final ClassMap classMap = getClassMap(value.getClass());
            final String methodBase = StringUtils.capitalizeFirstLetter(token);
            String methodName = "get" + methodBase;
            Method method = classMap.findMethod(methodName, ReflectionValueExtractor.CLASS_ARGS);
            if (method == null) {
                methodName = "is" + methodBase;
                method = classMap.findMethod(methodName, ReflectionValueExtractor.CLASS_ARGS);
            }
            if (method == null) {
                return null;
            }
            value = method.invoke(value, ReflectionValueExtractor.OBJECT_ARGS);
        }
        return value;
    }
    
    private static ClassMap getClassMap(final Class clazz) {
        ClassMap classMap = ReflectionValueExtractor.classMaps.get(clazz);
        if (classMap == null) {
            classMap = new ClassMap(clazz);
            ReflectionValueExtractor.classMaps.put(clazz, classMap);
        }
        return classMap;
    }
    
    static {
        CLASS_ARGS = new Class[0];
        OBJECT_ARGS = new Object[0];
        classMaps = new WeakHashMap();
    }
}
