// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.coverage.execute;

import org.pitest.util.Log;
import org.pitest.coverage.CoverageDatabase;
import org.pitest.mutationtest.config.TestPluginArguments;
import org.pitest.functional.prelude.Prelude;
import org.pitest.functional.F;
import java.util.concurrent.ExecutionException;
import java.io.IOException;
import java.net.ServerSocket;
import org.pitest.coverage.CoverageResult;
import org.pitest.functional.SideEffect1;
import java.util.List;
import org.pitest.util.PitError;
import org.pitest.util.ExitCode;
import org.pitest.process.ProcessArgs;
import org.pitest.util.SocketFinder;
import org.pitest.functional.FCollection;
import org.pitest.help.Help;
import org.pitest.classinfo.ClassInfo;
import org.pitest.util.Unchecked;
import org.pitest.help.PitHelpError;
import org.pitest.coverage.BlockCoverage;
import java.util.Collection;
import org.pitest.coverage.LineMap;
import org.pitest.coverage.analysis.LineMapper;
import org.pitest.coverage.CoverageData;
import org.pitest.coverage.CoverageExporter;
import java.io.File;
import org.pitest.util.Timings;
import org.pitest.classpath.CodeSource;
import org.pitest.process.LaunchOptions;
import java.util.logging.Logger;
import org.pitest.coverage.CoverageGenerator;

public class DefaultCoverageGenerator implements CoverageGenerator
{
    private static final Logger LOG;
    private final CoverageOptions coverageOptions;
    private final LaunchOptions launchOptions;
    private final CodeSource code;
    private final Timings timings;
    private final File workingDir;
    private final CoverageExporter exporter;
    private final boolean showProgress;
    
    public DefaultCoverageGenerator(final File workingDir, final CoverageOptions coverageOptions, final LaunchOptions launchOptions, final CodeSource code, final CoverageExporter exporter, final Timings timings, final boolean showProgress) {
        this.coverageOptions = coverageOptions;
        this.code = code;
        this.launchOptions = launchOptions;
        this.timings = timings;
        this.workingDir = workingDir;
        this.exporter = exporter;
        this.showProgress = showProgress;
    }
    
    @Override
    public CoverageData calculateCoverage() {
        try {
            final long t0 = System.currentTimeMillis();
            this.timings.registerStart(Timings.Stage.SCAN_CLASS_PATH);
            final Collection<ClassInfo> tests = this.code.getTests();
            this.timings.registerEnd(Timings.Stage.SCAN_CLASS_PATH);
            final CoverageData coverage = new CoverageData(this.code, new LineMapper(this.code));
            this.timings.registerStart(Timings.Stage.COVERAGE);
            this.gatherCoverageData(tests, coverage);
            this.timings.registerEnd(Timings.Stage.COVERAGE);
            final long time = (System.currentTimeMillis() - t0) / 1000L;
            DefaultCoverageGenerator.LOG.info("Calculated coverage in " + time + " seconds.");
            verifyBuildSuitableForMutationTesting(coverage);
            this.exporter.recordCoverage(coverage.createCoverage());
            return coverage;
        }
        catch (PitHelpError phe) {
            throw phe;
        }
        catch (Exception e) {
            throw Unchecked.translateCheckedException(e);
        }
    }
    
    private static void verifyBuildSuitableForMutationTesting(final CoverageData coverage) {
        if (!coverage.allTestsGreen()) {
            throw new PitHelpError(Help.FAILING_TESTS, new Object[0]);
        }
    }
    
    private void gatherCoverageData(final Collection<ClassInfo> tests, final CoverageData coverage) throws IOException, InterruptedException, ExecutionException {
        final List<String> filteredTests = FCollection.map(tests, classInfoToName());
        final SideEffect1<CoverageResult> handler = this.resultProcessor(coverage);
        final SocketFinder sf = new SocketFinder();
        final ServerSocket socket = sf.getNextAvailableServerSocket();
        final CoverageProcess process = new CoverageProcess(ProcessArgs.withClassPath(this.code.getClassPath()).andBaseDir(this.workingDir).andLaunchOptions(this.launchOptions).andStderr(logInfo()).andStdout(this.captureStandardOutIfVerbose()), this.coverageOptions, socket, filteredTests, handler);
        process.start();
        final ExitCode exitCode = process.waitToDie();
        if (exitCode == ExitCode.JUNIT_ISSUE) {
            DefaultCoverageGenerator.LOG.severe("Error generating coverage. Please check that your classpath contains JUnit 4.6 or above.");
            throw new PitError("Coverage generation minion exited abnormally. Please check the classpath.");
        }
        if (!exitCode.isOk()) {
            DefaultCoverageGenerator.LOG.severe("Coverage generator Minion exited abnormally due to " + exitCode);
            throw new PitError("Coverage generation minion exited abnormally!");
        }
        DefaultCoverageGenerator.LOG.fine("Coverage generator Minion exited ok");
    }
    
    private static F<ClassInfo, String> classInfoToName() {
        return new F<ClassInfo, String>() {
            @Override
            public String apply(final ClassInfo a) {
                return a.getName().asInternalName();
            }
        };
    }
    
    private SideEffect1<String> captureStandardOutIfVerbose() {
        if (this.coverageOptions.isVerbose()) {
            return log();
        }
        return Prelude.noSideEffect(String.class);
    }
    
    private static SideEffect1<String> logInfo() {
        return new SideEffect1<String>() {
            @Override
            public void apply(final String a) {
                DefaultCoverageGenerator.LOG.info("MINION : " + a);
            }
        };
    }
    
    private static SideEffect1<String> log() {
        return new SideEffect1<String>() {
            @Override
            public void apply(final String a) {
                DefaultCoverageGenerator.LOG.fine("MINION : " + a);
            }
        };
    }
    
    private SideEffect1<CoverageResult> resultProcessor(final CoverageData coverage) {
        return new SideEffect1<CoverageResult>() {
            private final String[] spinner = { "\b/", "\b-", "\b\\", "\b|" };
            int i = 0;
            
            @Override
            public void apply(final CoverageResult cr) {
                coverage.calculateClassCoverage(cr);
                if (DefaultCoverageGenerator.this.showProgress) {
                    System.out.printf("%s", this.spinner[this.i % this.spinner.length]);
                }
                ++this.i;
            }
        };
    }
    
    @Override
    public TestPluginArguments getConfiguration() {
        return this.coverageOptions.getPitConfig();
    }
    
    @Override
    public LaunchOptions getLaunchOptions() {
        return this.launchOptions;
    }
    
    static {
        LOG = Log.getLogger();
    }
}
