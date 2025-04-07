// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.config;

import java.util.Arrays;
import java.io.IOException;
import org.pitest.util.Unchecked;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.Reader;
import org.pitest.functional.Option;
import org.pitest.mutationtest.incremental.FileWriterFactory;
import org.pitest.mutationtest.incremental.NullWriterFactory;
import org.pitest.mutationtest.incremental.WriterFactory;
import org.pitest.util.ResultOutputStrategy;
import org.pitest.classpath.ClassPathRoot;
import org.pitest.classpath.PathFilter;
import org.pitest.classpath.ClassFilter;
import org.pitest.classpath.ProjectClassPaths;
import org.pitest.help.PitHelpError;
import org.pitest.help.Help;
import org.pitest.testapi.execute.Pitest;
import org.pitest.functional.prelude.Prelude;
import org.pitest.util.Glob;
import org.pitest.functional.F;
import org.pitest.functional.FCollection;
import org.pitest.classpath.ClassPath;
import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import org.pitest.testapi.TestGroupConfig;
import java.util.List;
import java.io.File;
import org.pitest.functional.predicate.Predicate;
import java.util.Collection;

public class ReportOptions
{
    public static final Collection<String> LOGGING_CLASSES;
    private Collection<String> targetClasses;
    private Collection<String> excludedMethods;
    private Collection<String> excludedClasses;
    private Collection<Predicate<String>> excludedTestClasses;
    private Collection<String> codePaths;
    private String reportDir;
    private File historyInputLocation;
    private File historyOutputLocation;
    private Collection<File> sourceDirs;
    private Collection<String> classPathElements;
    private Collection<String> mutators;
    private Collection<String> features;
    private int dependencyAnalysisMaxDistance;
    private final List<String> jvmArgs;
    private int numberOfThreads;
    private float timeoutFactor;
    private long timeoutConstant;
    private Collection<Predicate<String>> targetTests;
    private Collection<String> loggingClasses;
    private int maxMutationsPerClass;
    private boolean verbose;
    private boolean failWhenNoMutations;
    private final Collection<String> outputs;
    private TestGroupConfig groupConfig;
    private int mutationUnitSize;
    private boolean shouldCreateTimestampedReports;
    private boolean detectInlinedCode;
    private boolean exportLineCoverage;
    private int mutationThreshold;
    private int coverageThreshold;
    private String mutationEngine;
    private String javaExecutable;
    private boolean includeLaunchClasspath;
    private Properties properties;
    private int maxSurvivors;
    private Collection<String> excludedRunners;
    private Collection<String> includedTestMethods;
    private String testPlugin;
    
    public ReportOptions() {
        this.excludedMethods = (Collection<String>)Collections.emptyList();
        this.excludedClasses = (Collection<String>)Collections.emptyList();
        this.excludedTestClasses = (Collection<Predicate<String>>)Collections.emptyList();
        this.jvmArgs = new ArrayList<String>();
        this.numberOfThreads = 0;
        this.timeoutFactor = 1.25f;
        this.timeoutConstant = 4000L;
        this.loggingClasses = new ArrayList<String>();
        this.verbose = false;
        this.failWhenNoMutations = false;
        this.outputs = new LinkedHashSet<String>();
        this.shouldCreateTimestampedReports = true;
        this.detectInlinedCode = false;
        this.exportLineCoverage = false;
        this.mutationEngine = "gregor";
        this.includeLaunchClasspath = true;
        this.excludedRunners = new ArrayList<String>();
        this.includedTestMethods = new ArrayList<String>();
        this.testPlugin = "";
    }
    
    public boolean isVerbose() {
        return this.verbose;
    }
    
    public String getReportDir() {
        return this.reportDir;
    }
    
    public void setReportDir(final String reportDir) {
        this.reportDir = reportDir;
    }
    
    public Collection<File> getSourceDirs() {
        return this.sourceDirs;
    }
    
    public Collection<String> getClassPathElements() {
        return this.classPathElements;
    }
    
    public void setClassPathElements(final Collection<String> classPathElements) {
        this.classPathElements = classPathElements;
    }
    
    public void setSourceDirs(final Collection<File> sourceDirs) {
        this.sourceDirs = sourceDirs;
    }
    
    public Collection<String> getMutators() {
        return this.mutators;
    }
    
    public void setMutators(final Collection<String> mutators) {
        this.mutators = mutators;
    }
    
    public Collection<String> getFeatures() {
        return this.features;
    }
    
    public void setFeatures(final Collection<String> features) {
        this.features = features;
    }
    
    public int getDependencyAnalysisMaxDistance() {
        return this.dependencyAnalysisMaxDistance;
    }
    
    public void setDependencyAnalysisMaxDistance(final int dependencyAnalysisMaxDistance) {
        this.dependencyAnalysisMaxDistance = dependencyAnalysisMaxDistance;
    }
    
    public List<String> getJvmArgs() {
        return this.jvmArgs;
    }
    
    public void addChildJVMArgs(final List<String> args) {
        this.jvmArgs.addAll(args);
    }
    
    public ClassPath getClassPath() {
        if (this.classPathElements != null) {
            return this.createClassPathFromElements();
        }
        return new ClassPath();
    }
    
    private ClassPath createClassPathFromElements() {
        return new ClassPath(FCollection.map(this.classPathElements, stringToFile()));
    }
    
    private static F<String, File> stringToFile() {
        return new F<String, File>() {
            @Override
            public File apply(final String a) {
                return new File(a);
            }
        };
    }
    
    public Collection<String> getTargetClasses() {
        return this.targetClasses;
    }
    
    public Predicate<String> getTargetClassesFilter() {
        final Predicate<String> filter = (Predicate<String>)Prelude.and(Prelude.or(Glob.toGlobPredicates(this.targetClasses)), Prelude.not((F<Object, Boolean>)isBlackListed(Glob.toGlobPredicates(this.excludedClasses))));
        this.checkNotTryingToMutateSelf(filter);
        return filter;
    }
    
    private void checkNotTryingToMutateSelf(final Predicate<String> filter) {
        if (filter.apply(Pitest.class.getName())) {
            throw new PitHelpError(Help.BAD_FILTER, new Object[0]);
        }
    }
    
    public void setTargetClasses(final Collection<String> targetClasses) {
        this.targetClasses = targetClasses;
    }
    
    public void setTargetTests(final Collection<Predicate<String>> targetTestsPredicates) {
        this.targetTests = targetTestsPredicates;
    }
    
    public int getNumberOfThreads() {
        return this.numberOfThreads;
    }
    
    public void setNumberOfThreads(final int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }
    
    public float getTimeoutFactor() {
        return this.timeoutFactor;
    }
    
    public long getTimeoutConstant() {
        return this.timeoutConstant;
    }
    
    public void setTimeoutConstant(final long timeoutConstant) {
        this.timeoutConstant = timeoutConstant;
    }
    
    public void setTimeoutFactor(final float timeoutFactor) {
        this.timeoutFactor = timeoutFactor;
    }
    
    public Collection<Predicate<String>> getTargetTests() {
        return this.targetTests;
    }
    
    public Predicate<String> getTargetTestsFilter() {
        if (this.targetTests == null || this.targetTests.isEmpty()) {
            return (Predicate<String>)Prelude.and(Prelude.or(Glob.toGlobPredicates(this.targetClasses)), Prelude.not((F<Object, Boolean>)isBlackListed(this.excludedTestClasses)));
        }
        return (Predicate<String>)Prelude.and(Prelude.or(this.targetTests), Prelude.not((F<Object, Boolean>)isBlackListed(this.excludedTestClasses)));
    }
    
    private static Predicate<String> isBlackListed(final Collection<Predicate<String>> excludedClasses) {
        return Prelude.or(excludedClasses);
    }
    
    public Collection<String> getLoggingClasses() {
        if (this.loggingClasses.isEmpty()) {
            return ReportOptions.LOGGING_CLASSES;
        }
        return this.loggingClasses;
    }
    
    public void setLoggingClasses(final Collection<String> loggingClasses) {
        this.loggingClasses = loggingClasses;
    }
    
    public Collection<String> getExcludedMethods() {
        return this.excludedMethods;
    }
    
    public void setExcludedMethods(final Collection<String> excludedMethods) {
        this.excludedMethods = excludedMethods;
    }
    
    public void setVerbose(final boolean verbose) {
        this.verbose = verbose;
    }
    
    public void setExcludedClasses(final Collection<String> excludedClasses) {
        this.excludedClasses = excludedClasses;
    }
    
    public void setExcludedTestClasses(final Collection<Predicate<String>> excludedClasses) {
        this.excludedTestClasses = excludedClasses;
    }
    
    public void addOutputFormats(final Collection<String> formats) {
        this.outputs.addAll(formats);
    }
    
    public Collection<String> getOutputFormats() {
        return this.outputs;
    }
    
    public Collection<String> getExcludedClasses() {
        return this.excludedClasses;
    }
    
    public Collection<Predicate<String>> getExcludedTestClasses() {
        return this.excludedTestClasses;
    }
    
    public boolean shouldFailWhenNoMutations() {
        return this.failWhenNoMutations;
    }
    
    public void setFailWhenNoMutations(final boolean failWhenNoMutations) {
        this.failWhenNoMutations = failWhenNoMutations;
    }
    
    public ProjectClassPaths getMutationClassPaths() {
        return new ProjectClassPaths(this.getClassPath(), this.createClassesFilter(), this.createPathFilter());
    }
    
    public ClassFilter createClassesFilter() {
        return new ClassFilter(this.getTargetTestsFilter(), this.getTargetClassesFilter());
    }
    
    private PathFilter createPathFilter() {
        return new PathFilter(this.createCodePathFilter(), (Predicate<ClassPathRoot>)Prelude.not((F<Object, Boolean>)new DefaultDependencyPathPredicate()));
    }
    
    private Predicate<ClassPathRoot> createCodePathFilter() {
        if (this.codePaths != null && !this.codePaths.isEmpty()) {
            return new PathNamePredicate(Prelude.or(Glob.toGlobPredicates(this.codePaths)));
        }
        return new DefaultCodePathPredicate();
    }
    
    public Collection<String> getCodePaths() {
        return this.codePaths;
    }
    
    public void setCodePaths(final Collection<String> codePaths) {
        this.codePaths = codePaths;
    }
    
    public void setGroupConfig(final TestGroupConfig groupConfig) {
        this.groupConfig = groupConfig;
    }
    
    public TestGroupConfig getGroupConfig() {
        return this.groupConfig;
    }
    
    public int getMutationUnitSize() {
        return this.mutationUnitSize;
    }
    
    public void setMutationUnitSize(final int size) {
        this.mutationUnitSize = size;
    }
    
    public ResultOutputStrategy getReportDirectoryStrategy() {
        return new DirectoryResultOutputStrategy(this.getReportDir(), this.pickDirectoryStrategy());
    }
    
    public void setShouldCreateTimestampedReports(final boolean shouldCreateTimestampedReports) {
        this.shouldCreateTimestampedReports = shouldCreateTimestampedReports;
    }
    
    private ReportDirCreationStrategy pickDirectoryStrategy() {
        if (this.shouldCreateTimestampedReports) {
            return new DatedDirectoryReportDirCreationStrategy();
        }
        return new UndatedReportDirCreationStrategy();
    }
    
    public boolean shouldCreateTimeStampedReports() {
        return this.shouldCreateTimestampedReports;
    }
    
    public boolean isDetectInlinedCode() {
        return this.detectInlinedCode;
    }
    
    public void setDetectInlinedCode(final boolean b) {
        this.detectInlinedCode = b;
    }
    
    public WriterFactory createHistoryWriter() {
        if (this.historyOutputLocation == null) {
            return new NullWriterFactory();
        }
        return new FileWriterFactory(this.historyOutputLocation);
    }
    
    public Option<Reader> createHistoryReader() {
        if (this.historyInputLocation == null) {
            return (Option<Reader>)Option.none();
        }
        try {
            if (this.historyInputLocation.exists() && this.historyInputLocation.length() > 0L) {
                return (Option<Reader>)Option.some(new InputStreamReader(new FileInputStream(this.historyInputLocation), "UTF-8"));
            }
            return (Option<Reader>)Option.none();
        }
        catch (IOException ex) {
            throw Unchecked.translateCheckedException(ex);
        }
    }
    
    public void setHistoryInputLocation(final File historyInputLocation) {
        this.historyInputLocation = historyInputLocation;
    }
    
    public void setHistoryOutputLocation(final File historyOutputLocation) {
        this.historyOutputLocation = historyOutputLocation;
    }
    
    public File getHistoryInputLocation() {
        return this.historyInputLocation;
    }
    
    public File getHistoryOutputLocation() {
        return this.historyOutputLocation;
    }
    
    public void setExportLineCoverage(final boolean value) {
        this.exportLineCoverage = value;
    }
    
    public boolean shouldExportLineCoverage() {
        return this.exportLineCoverage;
    }
    
    public int getMutationThreshold() {
        return this.mutationThreshold;
    }
    
    public void setMutationThreshold(final int value) {
        this.mutationThreshold = value;
    }
    
    public String getMutationEngine() {
        return this.mutationEngine;
    }
    
    public void setMutationEngine(final String mutationEngine) {
        this.mutationEngine = mutationEngine;
    }
    
    public int getCoverageThreshold() {
        return this.coverageThreshold;
    }
    
    public void setCoverageThreshold(final int coverageThreshold) {
        this.coverageThreshold = coverageThreshold;
    }
    
    public String getJavaExecutable() {
        return this.javaExecutable;
    }
    
    public void setJavaExecutable(final String javaExecutable) {
        this.javaExecutable = javaExecutable;
    }
    
    public void setIncludeLaunchClasspath(final boolean b) {
        this.includeLaunchClasspath = b;
    }
    
    public boolean isIncludeLaunchClasspath() {
        return this.includeLaunchClasspath;
    }
    
    public Properties getFreeFormProperties() {
        return this.properties;
    }
    
    public void setFreeFormProperties(final Properties props) {
        this.properties = props;
    }
    
    public int getMaximumAllowedSurvivors() {
        return this.maxSurvivors;
    }
    
    public void setMaximumAllowedSurvivors(final int maxSurvivors) {
        this.maxSurvivors = maxSurvivors;
    }
    
    public Collection<String> getExcludedRunners() {
        return this.excludedRunners;
    }
    
    public Collection<String> getIncludedTestMethods() {
        return this.includedTestMethods;
    }
    
    public void setExcludedRunners(final Collection<String> excludedRunners) {
        this.excludedRunners = excludedRunners;
    }
    
    public void setIncludedTestMethods(final Collection<String> includedTestMethods) {
        this.includedTestMethods = includedTestMethods;
    }
    
    public TestPluginArguments createMinionSettings() {
        return new TestPluginArguments(this.getTestPlugin(), this.getGroupConfig(), this.getExcludedRunners(), this.getIncludedTestMethods());
    }
    
    public String getTestPlugin() {
        return this.testPlugin;
    }
    
    public void setTestPlugin(final String testPlugin) {
        this.testPlugin = testPlugin;
    }
    
    @Override
    public String toString() {
        return "ReportOptions [targetClasses=" + this.targetClasses + ", excludedMethods=" + this.excludedMethods + ", excludedClasses=" + this.excludedClasses + ", excludedTestClasses=" + this.excludedTestClasses + ", codePaths=" + this.codePaths + ", reportDir=" + this.reportDir + ", historyInputLocation=" + this.historyInputLocation + ", historyOutputLocation=" + this.historyOutputLocation + ", sourceDirs=" + this.sourceDirs + ", classPathElements=" + this.classPathElements + ", mutators=" + this.mutators + ", features=" + this.features + ", dependencyAnalysisMaxDistance=" + this.dependencyAnalysisMaxDistance + ", jvmArgs=" + this.jvmArgs + ", numberOfThreads=" + this.numberOfThreads + ", timeoutFactor=" + this.timeoutFactor + ", timeoutConstant=" + this.timeoutConstant + ", targetTests=" + this.targetTests + ", loggingClasses=" + this.loggingClasses + ", maxMutationsPerClass=" + this.maxMutationsPerClass + ", verbose=" + this.verbose + ", failWhenNoMutations=" + this.failWhenNoMutations + ", outputs=" + this.outputs + ", groupConfig=" + this.groupConfig + ", mutationUnitSize=" + this.mutationUnitSize + ", shouldCreateTimestampedReports=" + this.shouldCreateTimestampedReports + ", detectInlinedCode=" + this.detectInlinedCode + ", exportLineCoverage=" + this.exportLineCoverage + ", mutationThreshold=" + this.mutationThreshold + ", coverageThreshold=" + this.coverageThreshold + ", mutationEngine=" + this.mutationEngine + ", javaExecutable=" + this.javaExecutable + ", includeLaunchClasspath=" + this.includeLaunchClasspath + ", properties=" + this.properties + ", maxSurvivors=" + this.maxSurvivors + ", excludedRunners=" + this.excludedRunners + ", testPlugin=" + this.testPlugin + ", includedTestMethods=" + this.includedTestMethods + "]";
    }
    
    static {
        LOGGING_CLASSES = Arrays.asList("java.util.logging", "org.apache.log4j", "org.apache.logging.log4j", "org.slf4j", "edu.utdallas.prf.reloc.apache.commons.logging");
    }
}
