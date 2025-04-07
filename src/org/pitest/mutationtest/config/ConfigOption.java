// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.config;

import java.io.Serializable;

public enum ConfigOption
{
    TEST_PLUGIN("testPlugin"), 
    REPORT_DIR("reportDir"), 
    TARGET_CLASSES("targetClasses"), 
    SOURCE_DIR("sourceDirs"), 
    MUTATIONS("mutators"), 
    FEATURES("features"), 
    DEPENDENCY_DISTANCE("dependencyDistance", (Serializable)(-1)), 
    CHILD_JVM("jvmArgs"), 
    TIME_STAMPED_REPORTS("timestampedReports", (Serializable)true), 
    THREADS("threads", (Serializable)1), 
    TIMEOUT_FACTOR("timeoutFactor", (Serializable)1.25f), 
    TIMEOUT_CONST("timeoutConst", (Serializable)4000L), 
    TEST_FILTER("targetTests"), 
    AVOID_CALLS("avoidCallsTo"), 
    EXCLUDED_METHOD("excludedMethods"), 
    MAX_MUTATIONS_PER_CLASS("maxMutationsPerClass", (Serializable)0), 
    VERBOSE("verbose", (Serializable)false), 
    EXCLUDED_CLASSES("excludedClasses"), 
    EXCLUDED_TEST_CLASSES("excludedTestClasses"), 
    OUTPUT_FORMATS("outputFormats"), 
    CLASSPATH("classPath"), 
    CLASSPATH_FILE("classPathFile"), 
    FAIL_WHEN_NOT_MUTATIONS("failWhenNoMutations", (Serializable)true), 
    CODE_PATHS("mutableCodePaths"), 
    INCLUDED_GROUPS("includedGroups"), 
    INCLUDED_TEST_METHODS("includedTestMethods"), 
    EXCLUDED_GROUPS("excludedGroups"), 
    MUTATION_UNIT_SIZE("mutationUnitSize", (Serializable)0), 
    USE_INLINED_CODE_DETECTION("detectInlinedCode", (Serializable)true), 
    HISTORY_INPUT_LOCATION("historyInputLocation"), 
    HISTORY_OUTPUT_LOCATION("historyOutputLocation"), 
    MUTATION_THRESHOLD("mutationThreshold", (Serializable)0), 
    MAX_SURVIVING("maxSurviving", (Serializable)(-1)), 
    COVERAGE_THRESHOLD("coverageThreshold", (Serializable)0), 
    MUTATION_ENGINE("mutationEngine", (Serializable)"gregor"), 
    EXPORT_LINE_COVERAGE("exportLineCoverage", (Serializable)false), 
    INCLUDE_LAUNCH_CLASSPATH("includeLaunchClasspath", (Serializable)true), 
    JVM_PATH("jvmPath"), 
    PLUGIN_CONFIGURATION("pluginConfiguration");
    
    private final String text;
    private final Serializable defaultValue;
    
    private ConfigOption(final String text) {
        this(text, null);
    }
    
    private ConfigOption(final String text, final Serializable defaultValue) {
        this.text = text;
        this.defaultValue = defaultValue;
    }
    
    public String getParamName() {
        return this.text;
    }
    
    public <T> T getDefault(final Class<T> type) {
        return (T)this.defaultValue;
    }
    
    @Override
    public String toString() {
        return this.text;
    }
}
