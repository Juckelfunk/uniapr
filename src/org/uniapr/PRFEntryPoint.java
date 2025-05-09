// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr;

import org.pitest.mutationtest.tooling.KnownLocationJavaAgentFinder;
import org.pitest.mutationtest.tooling.JarCreatingJarFinder;
import org.pitest.process.JavaAgent;
import org.pitest.process.KnownLocationJavaExecutableLocator;
import edu.utdallas.prf.reloc.apache.commons.io.FileUtils;
import org.pitest.process.JavaExecutableLocator;
import org.uniapr.commons.process.LoggerUtils;
import org.pitest.process.LaunchOptions;
import org.pitest.classpath.ClassFilter;
import org.pitest.classpath.ClassPathRoot;
import org.pitest.functional.predicate.Predicate;
import org.pitest.functional.F;
import org.pitest.functional.prelude.Prelude;
import org.pitest.mutationtest.config.DefaultDependencyPathPredicate;
import org.pitest.mutationtest.config.DefaultCodePathPredicate;
import org.pitest.classpath.PathFilter;
import java.util.Collections;
import org.pitest.classinfo.ClassInfo;
import java.util.ArrayList;
import org.pitest.classpath.CodeSource;
import org.pitest.classpath.ProjectClassPaths;
import java.util.Iterator;
import java.util.Map;
import org.uniapr.profiler.ProfilerResults;
import org.pitest.process.ProcessArgs;
import org.uniapr.validator.PraPRTestComparator;
import org.uniapr.profiler.ProfilerDriver;
import java.io.File;
import java.util.List;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.classpath.ClassPath;

public class PRFEntryPoint
{
    private final ClassPath classPath;
    private final ClassByteArraySource byteArraySource;
    private final String whiteListPrefix;
    private final List<String> failingTests;
    private final File compatibleJREHome;
    private final long timeoutBias;
    private final double timeoutCoefficient;
    private final File patchesPool;
    private final PatchGenerationPlugin patchGenerationPluginImpl;
    private final boolean resetJVM;
    
    private PRFEntryPoint(final ClassPath classPath, final ClassByteArraySource byteArraySource, final String whiteListPrefix, final List<String> failingTests, final File compatibleJREHome, final long timeoutBias, final double timeoutCoefficient, final File patchesPool, final PatchGenerationPlugin patchGenerationPluginImpl, final boolean resetJVM) {
        this.classPath = classPath;
        this.byteArraySource = byteArraySource;
        this.whiteListPrefix = whiteListPrefix;
        this.failingTests = failingTests;
        this.compatibleJREHome = compatibleJREHome;
        this.timeoutBias = timeoutBias;
        this.timeoutCoefficient = timeoutCoefficient;
        this.patchesPool = patchesPool;
        this.patchGenerationPluginImpl = patchGenerationPluginImpl;
        this.resetJVM = resetJVM;
    }
    
    public static PRFEntryPoint createEntryPoint() {
        return new PRFEntryPoint(null, null, null, null, null, 0L, -1.0, null, null, false);
    }
    
    public PRFEntryPoint withClassPath(final ClassPath classPath) {
        return new PRFEntryPoint(classPath, this.byteArraySource, this.whiteListPrefix, this.failingTests, this.compatibleJREHome, this.timeoutBias, this.timeoutCoefficient, this.patchesPool, this.patchGenerationPluginImpl, this.resetJVM);
    }
    
    public PRFEntryPoint withWhiteListPrefix(final String whiteListPrefix) {
        return new PRFEntryPoint(this.classPath, this.byteArraySource, whiteListPrefix, this.failingTests, this.compatibleJREHome, this.timeoutBias, this.timeoutCoefficient, this.patchesPool, this.patchGenerationPluginImpl, this.resetJVM);
    }
    
    public PRFEntryPoint withFailingTests(final List<String> failingTests) {
        return new PRFEntryPoint(this.classPath, this.byteArraySource, this.whiteListPrefix, failingTests, this.compatibleJREHome, this.timeoutBias, this.timeoutCoefficient, this.patchesPool, this.patchGenerationPluginImpl, this.resetJVM);
    }
    
    public PRFEntryPoint withCompatibleJREHome(final File compatibleJREHome) {
        return new PRFEntryPoint(this.classPath, this.byteArraySource, this.whiteListPrefix, this.failingTests, compatibleJREHome, this.timeoutBias, this.timeoutCoefficient, this.patchesPool, this.patchGenerationPluginImpl, this.resetJVM);
    }
    
    public PRFEntryPoint withByteArraySource(final ClassByteArraySource byteArraySource) {
        return new PRFEntryPoint(this.classPath, byteArraySource, this.whiteListPrefix, this.failingTests, this.compatibleJREHome, this.timeoutBias, this.timeoutCoefficient, this.patchesPool, this.patchGenerationPluginImpl, this.resetJVM);
    }
    
    public PRFEntryPoint withTimeoutCoefficient(final double timeoutCoefficient) {
        return new PRFEntryPoint(this.classPath, this.byteArraySource, this.whiteListPrefix, this.failingTests, this.compatibleJREHome, this.timeoutBias, timeoutCoefficient, this.patchesPool, this.patchGenerationPluginImpl, this.resetJVM);
    }
    
    public PRFEntryPoint withPatchesPool(final File patchesPool) {
        return new PRFEntryPoint(this.classPath, this.byteArraySource, this.whiteListPrefix, this.failingTests, this.compatibleJREHome, this.timeoutBias, this.timeoutCoefficient, patchesPool, this.patchGenerationPluginImpl, this.resetJVM);
    }
    
    public PRFEntryPoint withPatchGenerationPlugin(final PatchGenerationPlugin patchGenerationPluginImpl) {
        return new PRFEntryPoint(this.classPath, this.byteArraySource, this.whiteListPrefix, this.failingTests, this.compatibleJREHome, this.timeoutBias, this.timeoutCoefficient, this.patchesPool, patchGenerationPluginImpl, this.resetJVM);
    }
    
    public PRFEntryPoint withTimeoutBias(final long timeoutBias) {
        return new PRFEntryPoint(this.classPath, this.byteArraySource, this.whiteListPrefix, this.failingTests, this.compatibleJREHome, timeoutBias, this.timeoutCoefficient, this.patchesPool, this.patchGenerationPluginImpl, this.resetJVM);
    }
    
    public PRFEntryPoint withResetJVM(final boolean resetJVM) {
        return new PRFEntryPoint(this.classPath, this.byteArraySource, this.whiteListPrefix, this.failingTests, this.compatibleJREHome, this.timeoutBias, this.timeoutCoefficient, this.patchesPool, this.patchGenerationPluginImpl, resetJVM);
    }
    
    public void run(final PatchGenerationPluginInfo info) throws Exception {
        if (this.patchGenerationPluginImpl != null) {
            this.patchGenerationPluginImpl.generatePatches(info.getCompatibleJDKHome(), this.patchesPool, info.getParams());
        }
        final ProcessArgs defaultProcessArgs = this.getDefaultProcessArgs();
        final List<String> appClassNames = this.retrieveApplicationClassNames();
        final ProfilerResults profilerResults = ProfilerDriver.runProfiler(defaultProcessArgs, appClassNames);
        final Map<String, Long> testsTiming = profilerResults.getTestsTiming();
        final PraPRTestComparator comparator = new PraPRTestComparator(testsTiming, profilerResults.getIsFailingTestPredicate());
        final PatchLoader patchLoader = new PatchLoader(this.patchesPool);
        final List<Patch> patches = patchLoader.loadPatches();
        final PatchValidationEngine engine = PatchValidationEngine.createEngine().forAppClassNames(appClassNames).forComparator(comparator).forPatches(patches).forProcessArgs(defaultProcessArgs).forTestsTiming(testsTiming).forTimeoutBias(this.timeoutBias).forTimeoutCoefficient(this.timeoutCoefficient).forResetJVM(this.resetJVM);
        final long start = System.currentTimeMillis();
        engine.test();
        System.out.println("***VALIDATION-TOOK: " + (System.currentTimeMillis() - start));
        System.out.println("# of plausible patches: " + this.getNumberOfPlausiblePatches(engine.getStatusTable()));
    }
    
    private int getNumberOfPlausiblePatches(final ValidationStatusTable statusTable) {
        int count = 0;
        for (final Map.Entry<PatchId, ValidationStatus> vs : statusTable.entrySet()) {
            if (vs.getValue() == ValidationStatus.PLAUSIBLE) {
                ++count;
            }
        }
        return count;
    }
    
    private List<String> retrieveApplicationClassNames() {
        final ProjectClassPaths pcp = new ProjectClassPaths(this.classPath, defaultClassFilter(this.whiteListPrefix), defaultPathFilter());
        final CodeSource codeSource = new CodeSource(pcp);
        final ArrayList<String> classNames = new ArrayList<String>();
        for (final ClassInfo classInfo : codeSource.getTests()) {
            classNames.add(classInfo.getName().asJavaName());
        }
        Collections.sort(classNames);
        classNames.trimToSize();
        return classNames;
    }
    
    private static PathFilter defaultPathFilter() {
        return new PathFilter(new DefaultCodePathPredicate(), (Predicate<ClassPathRoot>)Prelude.not(new DefaultDependencyPathPredicate()));
    }
    
    private static ClassFilter defaultClassFilter(final String whiteListPrefix) {
        final Predicate<String> filter = new Predicate<String>() {
            @Override
            public Boolean apply(final String cn) {
                return cn.startsWith(whiteListPrefix);
            }
        };
        return new ClassFilter(filter, filter);
    }
    
    private ProcessArgs getDefaultProcessArgs() {
        final LaunchOptions defaultLaunchOptions = new LaunchOptions(this.getJavaAgent(), this.getDefaultJavaExecutableLocator(), Collections.singletonList("-Xmx5g"), Collections.emptyMap());
        return ProcessArgs.withClassPath(this.classPath).andLaunchOptions(defaultLaunchOptions).andStderr(LoggerUtils.err()).andStdout(LoggerUtils.out());
    }
    
    private JavaExecutableLocator getDefaultJavaExecutableLocator() {
        final File javaFile = FileUtils.getFile(this.compatibleJREHome, "bin", "java");
        return new KnownLocationJavaExecutableLocator(javaFile.getAbsolutePath());
    }
    
    private JavaAgent getJavaAgent() {
        final String jarLocation = new JarCreatingJarFinder(this.byteArraySource).getJarLocation().value();
        return new KnownLocationJavaAgentFinder(jarLocation);
    }
}
