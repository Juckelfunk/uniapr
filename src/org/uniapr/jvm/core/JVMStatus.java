// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr.jvm.core;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;
import java.util.Properties;
import java.lang.reflect.Method;
import org.uniapr.jvm.agent.JVMClinitClassTransformer;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public class JVMStatus
{
    public static final String RECLINIT = "prapr_reclinit";
    public static final String PRAPR_GEN = "prapr_gen";
    public static final String LOCKCHECK = "lockedCheck";
    public static final String REFLOCKCHECK = "reflectionLockedCheck";
    public static final String CLASS = "org/uniapr/jvm/core/JVMStatus";
    public static ConcurrentMap<String, Integer> interfaces;
    public static int classId;
    public static final int MAX_CLASSNUM = 10000;
    public static boolean[] classStatusArray;
    public static ConcurrentMap<String, Integer> classIdMap;
    public static ConcurrentMap<Integer, String> idClassMap;
    public static Set<String> excludedPrefixes;
    public static Set<String> excludedTestPrefixes;
    
    public static void resetClinit() {
        resetSystemProperties();
        for (int i = 0; i < JVMStatus.classStatusArray.length; ++i) {
            JVMStatus.classStatusArray[i] = false;
        }
    }
    
    public static boolean lockedCheck(final int id) {
        if (JVMStatus.classStatusArray[id]) {
            return true;
        }
        synchronized (Integer.valueOf(id)) {
            if (JVMStatus.classStatusArray[id]) {
                return true;
            }
            JVMStatus.classStatusArray[id] = true;
            return false;
        }
    }
    
    public static void reflectionLockedCheck(final Class clazz) {
        final String slashName = clazz.getName().replace(".", "/");
        if (JVMClinitClassTransformer.leakingClasses.keySet().contains(slashName)) {
            final int id = registerClass(slashName);
            synchronized (clazz) {
                if (!JVMStatus.classStatusArray[id]) {
                    JVMStatus.classStatusArray[id] = true;
                    try {
                        final Method praprClinit = clazz.getMethod("prapr_reclinit", (Class[])null);
                        praprClinit.invoke(null, new Object[0]);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    public static synchronized int registerClass(final String slashClazz) {
        if (JVMStatus.classIdMap.containsKey(slashClazz)) {
            return JVMStatus.classIdMap.get(slashClazz);
        }
        final int id = nextId();
        JVMStatus.classIdMap.put(slashClazz, id);
        JVMStatus.idClassMap.put(id, slashClazz);
        return id;
    }
    
    private static synchronized int nextId() {
        return JVMStatus.classId++;
    }
    
    public static void resetSystemProperties() {
        System.setProperties(null);
        System.setSecurityManager(null);
    }
    
    public static boolean isExcluded(final String className) {
        for (final String prefix : JVMStatus.excludedPrefixes) {
            if (className.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isPrimitive(final String desc) {
        return desc.startsWith("L");
    }
    
    static {
        JVMStatus.interfaces = new ConcurrentHashMap<String, Integer>();
        JVMStatus.classId = 0;
        JVMStatus.classStatusArray = new boolean[10000];
        JVMStatus.classIdMap = new ConcurrentHashMap<String, Integer>();
        JVMStatus.idClassMap = new ConcurrentHashMap<Integer, String>();
        JVMStatus.excludedPrefixes = new HashSet<String>();
        JVMStatus.excludedTestPrefixes = new HashSet<String>();
        JVMStatus.excludedPrefixes.add("org/junit");
        JVMStatus.excludedPrefixes.add("junit/");
        JVMStatus.excludedPrefixes.add("org/apache/maven");
        JVMStatus.excludedPrefixes.add("org/mudebug");
        JVMStatus.excludedPrefixes.add("org/pitest");
        JVMStatus.excludedPrefixes.add("org/uniapr");
        JVMStatus.excludedPrefixes.add("org/objectweb");
        JVMStatus.excludedPrefixes.add("org/codehaus");
        JVMStatus.excludedPrefixes.add("org/prapr");
        JVMStatus.excludedPrefixes.add("java/");
        JVMStatus.excludedPrefixes.add("javax/");
        JVMStatus.excludedPrefixes.add("sun/");
        JVMStatus.excludedPrefixes.add("com/sun/");
        JVMStatus.excludedPrefixes.add("org/xml/");
        JVMStatus.excludedPrefixes.add("org/w3c/");
        JVMStatus.excludedPrefixes.add("jdk/");
        JVMStatus.excludedPrefixes.add("netscape/");
        JVMStatus.excludedPrefixes.add("org/ietf/");
        JVMStatus.excludedPrefixes.add("org/mockito/cglib/core");
        JVMStatus.excludedPrefixes.add("org/jfree/data/time/RegularTimePeriod");
        JVMStatus.excludedPrefixes.add("org/jfree/chart/util/ShapeUtilities");
        JVMStatus.excludedPrefixes.add("org/jfree/chart/util/UnitType");
        final String separator = "/";
        JVMStatus.excludedPrefixes.add("org" + separator + "apache/commons/lang3/reflect/testbed/");
        JVMStatus.excludedPrefixes.add("org" + separator + "apache/commons/lang/reflect/testbed");
        JVMStatus.excludedPrefixes.add("org" + separator + "apache/commons/lang/enum");
        final String dot = ".";
        JVMStatus.excludedTestPrefixes.add("org" + dot + "apache.commons.lang.EntitiesPerformanceTest");
        JVMStatus.excludedTestPrefixes.add("org" + dot + "apache.commons.lang.builder.ToStringBuilderTest");
        JVMStatus.excludedTestPrefixes.add("org" + dot + "joda.time.TestDateMidnight_Basics");
        JVMStatus.excludedTestPrefixes.add("org" + dot + "apache.commons.lang3.EntitiesPerformanceTest");
        JVMStatus.excludedTestPrefixes.add("org" + dot + "apache.commons.lang3.builder.ToStringBuilderTest");
        JVMStatus.excludedTestPrefixes.add("org" + dot + "jfree.data.time.junit.TimeSeriesTests");
    }
}
