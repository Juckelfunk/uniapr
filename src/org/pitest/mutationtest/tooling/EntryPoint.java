// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.tooling;

import java.util.Iterator;
import org.pitest.plugin.FeatureParameter;
import org.pitest.plugin.Feature;
import org.pitest.functional.SideEffect1;
import org.pitest.mutationtest.HistoryStore;
import org.pitest.coverage.CoverageGenerator;
import org.pitest.classpath.ProjectClassPaths;
import org.pitest.coverage.execute.CoverageOptions;
import org.pitest.mutationtest.MutationResultListenerFactory;
import org.pitest.util.ResultOutputStrategy;
import org.pitest.mutationtest.incremental.WriterFactory;
import java.io.Reader;
import org.pitest.functional.Option;
import org.pitest.classpath.ClassPath;
import java.io.IOException;
import org.pitest.mutationtest.incremental.ObjectOutputStreamHistoryStore;
import org.pitest.coverage.execute.DefaultCoverageGenerator;
import org.pitest.util.Timings;
import org.pitest.classpath.CodeSource;
import org.pitest.process.JavaAgent;
import org.pitest.process.LaunchOptions;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.classpath.ClassPathByteArraySource;
import org.pitest.util.Log;
import org.pitest.mutationtest.config.SettingsFactory;
import java.util.Map;
import org.pitest.mutationtest.config.PluginServices;
import org.pitest.mutationtest.config.ReportOptions;
import java.io.File;

public class EntryPoint
{
    public AnalysisResult execute(final File baseDir, final ReportOptions data, final PluginServices plugins, final Map<String, String> environmentVariables) {
        final SettingsFactory settings = new SettingsFactory(data, plugins);
        return this.execute(baseDir, data, settings, environmentVariables);
    }
    
    public AnalysisResult execute(final File baseDir, final ReportOptions data, final SettingsFactory settings, final Map<String, String> environmentVariables) {
        if (data.isVerbose()) {
            Log.getLogger().info("---------------------------------------------------------------------------");
            Log.getLogger().info("Enabled (+) and disabled (-) features.");
            Log.getLogger().info("-----------------------------------------");
            settings.describeFeatures(this.asInfo("+"), this.asInfo("-"));
            Log.getLogger().info("---------------------------------------------------------------------------");
        }
        this.selectTestPlugin(data);
        final ClassPath cp = data.getClassPath();
        final Option<Reader> reader = data.createHistoryReader();
        final WriterFactory historyWriter = data.createHistoryWriter();
        final JavaAgent jac = new JarCreatingJarFinder(new ClassPathByteArraySource(cp));
        final KnownLocationJavaAgentFinder ja = new KnownLocationJavaAgentFinder(jac.getJarLocation().value());
        final ResultOutputStrategy reportOutput = settings.getOutputStrategy();
        final MutationResultListenerFactory reportFactory = settings.createListener();
        final CoverageOptions coverageOptions = settings.createCoverageOptions();
        final LaunchOptions launchOptions = new LaunchOptions(ja, settings.getJavaExecutable(), data.getJvmArgs(), environmentVariables);
        final ProjectClassPaths cps = data.getMutationClassPaths();
        final CodeSource code = new CodeSource(cps);
        final Timings timings = new Timings();
        final CoverageGenerator coverageDatabase = new DefaultCoverageGenerator(baseDir, coverageOptions, launchOptions, code, settings.createCoverageExporter(), timings, !data.isVerbose());
        final HistoryStore history = new ObjectOutputStreamHistoryStore(historyWriter, reader);
        final MutationStrategies strategies = new MutationStrategies(settings.createEngine(), history, coverageDatabase, reportFactory, reportOutput);
        final MutationCoverage report = new MutationCoverage(strategies, baseDir, code, data, settings, timings);
        try {
            return AnalysisResult.success(report.runReport());
        }
        catch (IOException e) {
            return AnalysisResult.fail(e);
        }
        finally {
            jac.close();
            ja.close();
            historyWriter.close();
        }
    }
    
    private void selectTestPlugin(final ReportOptions data) {
        if (data.getTestPlugin() == null || data.getTestPlugin().equals("")) {
            if (this.junit5PluginIsOnClasspath()) {
                data.setTestPlugin("junit5");
            }
            else {
                data.setTestPlugin("junit");
            }
        }
    }
    
    private boolean junit5PluginIsOnClasspath() {
        try {
            Class.forName("org.pitest.junit5.JUnit5TestPluginFactory");
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    private SideEffect1<Feature> asInfo(final String leader) {
        return new SideEffect1<Feature>() {
            @Override
            public void apply(final Feature a) {
                Log.getLogger().info(String.format("%1$-16s", leader + a.name()) + a.description());
                for (final FeatureParameter each : a.params()) {
                    Log.getLogger().info(String.format("%1$-18s", "  [" + each.name() + "]") + each.description());
                }
            }
        };
    }
}
