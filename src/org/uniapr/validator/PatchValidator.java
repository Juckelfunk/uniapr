// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr.validator;

import org.uniapr.ValidationStatusTable;
import java.util.Map;
import java.util.Collection;
import org.pitest.process.ProcessArgs;
import org.pitest.mutationtest.execute.MemoryWatchdog;
import javax.management.Notification;
import javax.management.NotificationListener;
import org.uniapr.PatchId;
import java.util.Iterator;
import org.pitest.classinfo.ClassByteArraySource;
import java.io.IOException;
import org.pitest.util.ExitCode;
import org.uniapr.jvm.core.JVMStatus;
import org.uniapr.ValidationStatus;
import org.uniapr.junit.runner.JUnitRunner;
import org.uniapr.Patch;
import java.lang.instrument.ClassFileTransformer;
import org.pitest.boot.HotSwapAgent;
import org.uniapr.jvm.agent.JVMClinitClassTransformer;
import org.pitest.util.SafeDataInputStream;
import org.pitest.classinfo.CachingByteArraySource;
import org.pitest.classpath.ClassloaderByteArraySource;
import org.pitest.util.IsolationUtils;
import java.net.Socket;

public class PatchValidator
{
    private static final int CACHE_SIZE = 50;
    
    public static void main(final String[] args) {
        System.out.println("Patch Validator is HERE!");
        final int port = Integer.parseInt(args[0]);
        try (final Socket socket = new Socket("localhost", port)) {
            final ClassLoader loader = IsolationUtils.getContextClassLoader();
            ClassByteArraySource byteArraySource = new ClassloaderByteArraySource(loader);
            byteArraySource = new CachingByteArraySource(byteArraySource, 50);
            final SafeDataInputStream dis = new SafeDataInputStream(socket.getInputStream());
            final ValidatorArguments arguments = dis.read(ValidatorArguments.class);
            final ValidatorReporter reporter = new ValidatorReporter(socket.getOutputStream());
            if (arguments.shouldResetJVM()) {
                System.out.println(">>Installing JVM ClInit Class Transformer...");
                HotSwapAgent.addTransformer(new JVMClinitClassTransformer());
            }
            addMemoryWatchDog(reporter);
            for (final Patch patch : arguments.getPatches()) {
                System.out.println(">>Validating patchID: " + patch.patchName);
                final JUnitRunner runner = new JUnitRunner(arguments.getAppClassNames(), arguments.getTestComparator(), true);
                final PatchId id = patch.getId();
                reporter.notifyStarted(id);
                try {
                    final int res = patch.test(byteArraySource, loader, runner, arguments.getTestsTiming(), arguments.getTimeoutBias(), arguments.getTimeoutCoefficient());
                    ValidationStatus status;
                    if (res == 1) {
                        status = ValidationStatus.PLAUSIBLE;
                    }
                    else if (res == 0) {
                        status = ValidationStatus.NON_PLAUSIBLE;
                    }
                    else if (res == -1) {
                        status = ValidationStatus.TIMED_OUT;
                    }
                    else {
                        status = ValidationStatus.INITIALIZER_ERROR;
                    }
                    reporter.reportStatus(id, status);
                    if (status == ValidationStatus.TIMED_OUT || status == ValidationStatus.INITIALIZER_ERROR) {
                        break;
                    }
                }
                catch (Throwable t) {
                    t.printStackTrace();
                    reporter.reportStatus(id, ValidationStatus.RUN_ERROR);
                    break;
                }
                if (arguments.shouldResetJVM()) {
                    JVMStatus.resetClinit();
                }
            }
            System.out.println("Patch Validator is DONE!");
            reporter.done(ExitCode.OK);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Patch Validator is unable to establish connection!");
        }
    }
    
    private static void addMemoryWatchDog(final ValidatorReporter reporter) {
        final NotificationListener listener = new NotificationListener() {
            @Override
            public void handleNotification(final Notification notification, final Object handback) {
                final String type = notification.getType();
                if (type.equals("java.management.memory.threshold.exceeded")) {
                    reporter.done(ExitCode.OUT_OF_MEMORY);
                }
            }
        };
        MemoryWatchdog.addWatchDogToAllPools(90L, listener);
    }
    
    public static ValidationStatusTable testPatches(final ProcessArgs defaultProcessArgs, final PraPRTestComparator testComparator, final Collection<String> appClassNames, final Collection<Patch> patches, final Map<String, Long> methodsTiming, final long timeoutBias, final double timeoutCoefficient, final boolean resetJVM) {
        final ValidatorArguments arguments = new ValidatorArguments(testComparator, appClassNames, patches, methodsTiming, timeoutBias, timeoutCoefficient, resetJVM);
        final ValidatorProcess process = new ValidatorProcess(defaultProcessArgs, arguments);
        process.checkInPatches(patches);
        try {
            process.start();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
        final ExitCode exitCode = process.waitToDie();
        final ValidationStatusTable statusTable = process.getStatusTable();
        if (exitCode != ExitCode.OK) {
            final ValidationStatus status = ValidationStatus.forExitCode(exitCode);
            for (final Map.Entry<PatchId, ValidationStatus> entry : statusTable.entrySet()) {
                if (entry.getValue() == ValidationStatus.STARTED) {
                    entry.setValue(status);
                    break;
                }
            }
        }
        return statusTable;
    }
}
