// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.introspection;

import java.util.WeakHashMap;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Arrays;
import org.codehaus.plexus.util.StringUtils;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.Map;

public class ReflectionValueExtractor
{
    private static final Class[] CLASS_ARGS;
    private static final Object[] OBJECT_ARGS;
    private static final Map classMaps;
    private static final Pattern INDEXED_PROPS;
    private static final Pattern MAPPED_PROPS;
    
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
            if (value == null) {
                return null;
            }
            final String token = parser.nextToken();
            ClassMap classMap = getClassMap(value.getClass());
            Object[] localParams = ReflectionValueExtractor.OBJECT_ARGS;
            Matcher matcher = ReflectionValueExtractor.INDEXED_PROPS.matcher(token);
            Method method;
            if (matcher.find()) {
                final String methodBase = StringUtils.capitalizeFirstLetter(matcher.group(1));
                final String methodName = "get" + methodBase;
                method = classMap.findMethod(methodName, ReflectionValueExtractor.CLASS_ARGS);
                value = method.invoke(value, ReflectionValueExtractor.OBJECT_ARGS);
                classMap = getClassMap(value.getClass());
                if (classMap.getCachedClass().isArray()) {
                    value = Arrays.asList((Object[])value);
                    classMap = getClassMap(value.getClass());
                }
                if (!(value instanceof List)) {
                    throw new Exception("The token '" + token + "' refers to a java.util.List or an array, but the value seems is an instance of '" + value.getClass() + "'.");
                }
                localParams = new Object[] { Integer.valueOf(matcher.group(2)) };
                method = classMap.findMethod("get", localParams);
            }
            else {
                matcher = ReflectionValueExtractor.MAPPED_PROPS.matcher(token);
                if (matcher.find()) {
                    final String methodBase = StringUtils.capitalizeFirstLetter(matcher.group(1));
                    final String methodName = "get" + methodBase;
                    method = classMap.findMethod(methodName, ReflectionValueExtractor.CLASS_ARGS);
                    value = method.invoke(value, ReflectionValueExtractor.OBJECT_ARGS);
                    classMap = getClassMap(value.getClass());
                    if (!(value instanceof Map)) {
                        throw new Exception("The token '" + token + "' refers to a java.util.Map, but the value seems is an instance of '" + value.getClass() + "'.");
                    }
                    localParams = new Object[] { matcher.group(2) };
                    method = classMap.findMethod("get", localParams);
                }
                else {
                    final String methodBase = StringUtils.capitalizeFirstLetter(token);
                    String methodName = "get" + methodBase;
                    method = classMap.findMethod(methodName, ReflectionValueExtractor.CLASS_ARGS);
                    if (method == null) {
                        methodName = "is" + methodBase;
                        method = classMap.findMethod(methodName, ReflectionValueExtractor.CLASS_ARGS);
                    }
                }
            }
            if (method == null) {
                return null;
            }
            try {
                value = method.invoke(value, localParams);
            }
            catch (InvocationTargetException e) {
                if (e.getCause() instanceof IndexOutOfBoundsException) {
                    return null;
                }
                throw e;
            }
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
        INDEXED_PROPS = Pattern.compile("(\\w+)\\[(\\d+)\\]");
        MAPPED_PROPS = Pattern.compile("(\\w+)\\((.+)\\)");
    }
}
