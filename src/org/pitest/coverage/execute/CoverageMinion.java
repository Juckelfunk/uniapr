// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.coverage.execute;

import java.util.ArrayList;
import org.pitest.classpath.ClassloaderByteArraySource;
import org.pitest.mutationtest.config.MinionSettings;
import org.pitest.mutationtest.config.ClientPluginServices;
import org.pitest.util.IsolationUtils;
import org.pitest.functional.F;
import org.pitest.functional.FCollection;
import org.pitest.testapi.execute.FindTestUnits;
import org.pitest.testapi.Configuration;
import org.pitest.classinfo.ClassName;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.dependency.DependencyExtractor;
import org.pitest.classpath.ClassPathByteArraySource;
import java.util.Collections;
import org.pitest.mutationtest.mocksupport.BendJavassistToMyWillTransformer;
import org.pitest.mutationtest.mocksupport.JavassistInputStreamInterceptorAdapater;
import org.pitest.functional.prelude.Prelude;
import org.pitest.util.Glob;
import org.pitest.functional.predicate.Predicate;
import org.pitest.testapi.TestUnit;
import java.util.List;
import java.io.IOException;
import org.pitest.util.Unchecked;
import org.pitest.help.PitHelpError;
import java.util.logging.Level;
import java.lang.instrument.ClassFileTransformer;
import org.pitest.boot.HotSwapAgent;
import org.pitest.coverage.CoverageTransformer;
import sun.pitest.InvokeReceiver;
import sun.pitest.CodeCoverageStore;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import org.pitest.util.Log;
import org.pitest.util.SafeDataInputStream;
import java.net.Socket;
import org.pitest.util.ExitCode;
import java.util.logging.Logger;

public class CoverageMinion
{
    private static final Logger LOG;
    
    public static void main(final String[] args) {
        enablePowerMockSupport();
        ExitCode exitCode = ExitCode.OK;
        Socket s = null;
        CoveragePipe invokeQueue = null;
        try {
            final int port = Integer.parseInt(args[0]);
            s = new Socket("localhost", port);
            final SafeDataInputStream dis = new SafeDataInputStream(s.getInputStream());
            final CoverageOptions paramsFromParent = dis.read(CoverageOptions.class);
            Log.setVerbose(paramsFromParent.isVerbose());
            invokeQueue = new CoveragePipe(new BufferedOutputStream(s.getOutputStream()));
            CodeCoverageStore.init(invokeQueue);
            HotSwapAgent.addTransformer(new CoverageTransformer(convertToJVMClassFilter(paramsFromParent.getFilter())));
            final List<TestUnit> tus = getTestsFromParent(dis, paramsFromParent);
            CoverageMinion.LOG.info(tus.size() + " tests received");
            final CoverageWorker worker = new CoverageWorker(invokeQueue, tus);
            worker.run();
        }
        catch (PitHelpError phe) {
            CoverageMinion.LOG.log(Level.SEVERE, phe.getMessage());
            exitCode = ExitCode.JUNIT_ISSUE;
        }
        catch (Throwable ex) {
            ex.printStackTrace(System.out);
            CoverageMinion.LOG.log(Level.SEVERE, "Error calculating coverage. Process will exit.", ex);
            exitCode = ExitCode.UNKNOWN_ERROR;
        }
        finally {
            if (invokeQueue != null) {
                invokeQueue.end(exitCode);
            }
            try {
                if (s != null) {
                    s.close();
                }
            }
            catch (IOException e) {
                throw Unchecked.translateCheckedException(e);
            }
        }
        System.exit(exitCode.getCode());
    }
    
    private static void enablePowerMockSupport() {
        HotSwapAgent.addTransformer(new BendJavassistToMyWillTransformer((Predicate<String>)Prelude.or(new Glob("javassist/*")), JavassistInputStreamInterceptorAdapater.inputStreamAdapterSupplier(JavassistCoverageInterceptor.class)));
    }
    
    private static Predicate<String> convertToJVMClassFilter(final Predicate<String> child) {
        return new Predicate<String>() {
            @Override
            public Boolean apply(final String a) {
                return (Boolean)child.apply(a.replace("/", "."));
            }
        };
    }
    
    private static List<TestUnit> getTestsFromParent(final SafeDataInputStream dis, final CoverageOptions paramsFromParent) throws IOException {
        final List<ClassName> classes = receiveTestClassesFromParent(dis);
        Collections.sort(classes);
        final Configuration testPlugin = createTestPlugin(paramsFromParent);
        verifyEnvironment(testPlugin);
        final List<TestUnit> tus = discoverTests(testPlugin, classes);
        final DependencyFilter filter = new DependencyFilter(new DependencyExtractor(new ClassPathByteArraySource(), paramsFromParent.getDependencyAnalysisMaxDistance()), paramsFromParent.getFilter());
        final List<TestUnit> filteredTus = filter.filterTestsByDependencyAnalysis(tus);
        CoverageMinion.LOG.info("Dependency analysis reduced number of potential tests by " + (tus.size() - filteredTus.size()));
        return filteredTus;
    }
    
    private static List<TestUnit> discoverTests(final Configuration testPlugin, final List<ClassName> classes) {
        final FindTestUnits finder = new FindTestUnits(testPlugin);
        final List<TestUnit> tus = finder.findTestUnitsForAllSuppliedClasses((Iterable<Class<?>>)FCollection.flatMap((Iterable<? extends ClassName>)classes, (F<ClassName, ? extends Iterable<Object>>)ClassName.nameToClass()));
        CoverageMinion.LOG.info("Found  " + tus.size() + " tests");
        return tus;
    }
    
    private static Configuration createTestPlugin(final CoverageOptions paramsFromParent) {
        final ClientPluginServices plugins = new ClientPluginServices(IsolationUtils.getContextClassLoader());
        final MinionSettings factory = new MinionSettings(plugins);
        final Configuration testPlugin = factory.getTestFrameworkPlugin(paramsFromParent.getPitConfig(), ClassloaderByteArraySource.fromContext());
        return testPlugin;
    }
    
    private static void verifyEnvironment(final Configuration config) {
        CoverageMinion.LOG.info("Checking environment");
        if (config.verifyEnvironment().hasSome()) {
            throw config.verifyEnvironment().value();
        }
    }
    
    private static List<ClassName> receiveTestClassesFromParent(final SafeDataInputStream dis) {
        final int count = dis.readInt();
        CoverageMinion.LOG.fine("Expecting " + count + " tests classes from parent");
        final List<ClassName> classes = new ArrayList<ClassName>(count);
        for (int i = 0; i != count; ++i) {
            classes.add(ClassName.fromString(dis.readString()));
        }
        CoverageMinion.LOG.fine("Tests classes received");
        return classes;
    }
    
    static {
        LOG = Log.getLogger();
    }
}
