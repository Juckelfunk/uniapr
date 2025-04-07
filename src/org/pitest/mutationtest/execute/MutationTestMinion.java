// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.execute;

import java.lang.management.MemoryNotificationInfo;
import javax.management.openmbean.CompositeData;
import javax.management.Notification;
import javax.management.NotificationListener;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import org.pitest.boot.HotSwapAgent;
import org.pitest.mutationtest.mocksupport.BendJavassistToMyWillTransformer;
import org.pitest.mutationtest.mocksupport.JavassistInputStreamInterceptorAdapater;
import org.pitest.mutationtest.mocksupport.JavassistInterceptor;
import org.pitest.functional.prelude.Prelude;
import org.pitest.util.Glob;
import org.pitest.functional.predicate.Predicate;
import org.pitest.testapi.execute.FindTestUnits;
import org.pitest.functional.F;
import org.pitest.functional.FCollection;
import java.util.Collection;
import org.pitest.mutationtest.config.ClientPluginServices;
import java.net.Socket;
import org.pitest.testapi.Configuration;
import org.pitest.mutationtest.config.TestPluginArguments;
import org.pitest.mutationtest.EngineArguments;
import org.pitest.testapi.TestUnit;
import java.util.List;
import org.pitest.mutationtest.engine.MutationEngine;
import org.pitest.classinfo.ClassName;
import org.pitest.functional.F3;
import java.util.logging.Level;
import org.pitest.util.ExitCode;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.classinfo.CachingByteArraySource;
import org.pitest.classpath.ClassloaderByteArraySource;
import org.pitest.util.IsolationUtils;
import org.pitest.util.Log;
import org.pitest.mutationtest.config.MinionSettings;
import org.pitest.util.SafeDataInputStream;
import java.util.logging.Logger;

public class MutationTestMinion
{
    private static final Logger LOG;
    private static final int CACHE_SIZE = 12;
    private final SafeDataInputStream dis;
    private final Reporter reporter;
    private final MinionSettings plugins;
    
    public MutationTestMinion(final MinionSettings plugins, final SafeDataInputStream dis, final Reporter reporter) {
        this.dis = dis;
        this.reporter = reporter;
        this.plugins = plugins;
    }
    
    public void run() {
        try {
            final MinionArguments paramsFromParent = this.dis.read(MinionArguments.class);
            Log.setVerbose(paramsFromParent.isVerbose());
            final ClassLoader loader = IsolationUtils.getContextClassLoader();
            final ClassByteArraySource byteSource = new CachingByteArraySource(new ClassloaderByteArraySource(loader), 12);
            final F3<ClassName, ClassLoader, byte[], Boolean> hotswap = new HotSwap(byteSource);
            final MutationEngine engine = this.createEngine(paramsFromParent.engine, paramsFromParent.engineArgs);
            final MutationTestWorker worker = new MutationTestWorker(hotswap, engine.createMutator(byteSource), loader);
            final List<TestUnit> tests = findTestsForTestClasses(loader, paramsFromParent.testClasses, this.createTestPlugin(paramsFromParent.pitConfig));
            worker.run(paramsFromParent.mutations, this.reporter, new TimeOutDecoratedTestSource(paramsFromParent.timeoutStrategy, tests, this.reporter));
            this.reporter.done(ExitCode.OK);
        }
        catch (Throwable ex) {
            ex.printStackTrace(System.out);
            MutationTestMinion.LOG.log(Level.WARNING, "Error during mutation test", ex);
            this.reporter.done(ExitCode.UNKNOWN_ERROR);
        }
    }
    
    private MutationEngine createEngine(final String engine, final EngineArguments args) {
        return this.plugins.createEngine(engine).createEngine(args);
    }
    
    private Configuration createTestPlugin(final TestPluginArguments pitConfig) {
        return this.plugins.getTestFrameworkPlugin(pitConfig, ClassloaderByteArraySource.fromContext());
    }
    
    public static void main(final String[] args) {
        MutationTestMinion.LOG.log(Level.FINE, "minion started");
        enablePowerMockSupport();
        final int port = Integer.valueOf(args[0]);
        Socket s = null;
        try {
            s = new Socket("localhost", port);
            final SafeDataInputStream dis = new SafeDataInputStream(s.getInputStream());
            final Reporter reporter = new DefaultReporter(s.getOutputStream());
            addMemoryWatchDog(reporter);
            final ClientPluginServices plugins = new ClientPluginServices(IsolationUtils.getContextClassLoader());
            final MinionSettings factory = new MinionSettings(plugins);
            final MutationTestMinion instance = new MutationTestMinion(factory, dis, reporter);
            instance.run();
        }
        catch (Throwable ex) {
            ex.printStackTrace(System.out);
            MutationTestMinion.LOG.log(Level.WARNING, "Error during mutation test", ex);
        }
        finally {
            if (s != null) {
                safelyCloseSocket(s);
            }
        }
    }
    
    private static List<TestUnit> findTestsForTestClasses(final ClassLoader loader, final Collection<ClassName> testClasses, final Configuration pitConfig) {
        final Collection<Class<?>> tcs = (Collection<Class<?>>)FCollection.flatMap((Iterable<? extends ClassName>)testClasses, (F<ClassName, ? extends Iterable<Object>>)ClassName.nameToClass(loader));
        final FindTestUnits finder = new FindTestUnits(pitConfig);
        return finder.findTestUnitsForAllSuppliedClasses(tcs);
    }
    
    private static void enablePowerMockSupport() {
        HotSwapAgent.addTransformer(new BendJavassistToMyWillTransformer((Predicate<String>)Prelude.or(new Glob("javassist/*")), JavassistInputStreamInterceptorAdapater.inputStreamAdapterSupplier(JavassistInterceptor.class)));
    }
    
    private static void safelyCloseSocket(final Socket s) {
        if (s != null) {
            try {
                s.close();
            }
            catch (IOException e) {
                MutationTestMinion.LOG.log(Level.WARNING, "Couldn't close socket", e);
            }
        }
    }
    
    private static void addMemoryWatchDog(final Reporter r) {
        final NotificationListener listener = new NotificationListener() {
            @Override
            public void handleNotification(final Notification notification, final Object handback) {
                final String type = notification.getType();
                if (type.equals("java.management.memory.threshold.exceeded")) {
                    final CompositeData cd = (CompositeData)notification.getUserData();
                    final MemoryNotificationInfo memInfo = MemoryNotificationInfo.from(cd);
                    CommandLineMessage.report(memInfo.getPoolName() + " has exceeded the shutdown threshold : " + memInfo.getCount() + " times.\n" + memInfo.getUsage());
                    r.done(ExitCode.OUT_OF_MEMORY);
                }
                else {
                    MutationTestMinion.LOG.warning("Unknown notification: " + notification);
                }
            }
        };
        MemoryWatchdog.addWatchDogToAllPools(90L, listener);
    }
    
    static {
        LOG = Log.getLogger();
    }
}
