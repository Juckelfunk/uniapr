// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.junit.adapter;

import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.pitest.testapi.ResultCollector;
import org.junit.runner.Runner;
import org.pitest.testapi.Description;

public class CustomRunnerExecutor
{
    private final Description description;
    private final Runner runner;
    private final ResultCollector rc;
    
    public CustomRunnerExecutor(final Description description, final Runner runner, final ResultCollector rc) {
        this.runner = runner;
        this.rc = rc;
        this.description = description;
    }
    
    public void run() {
        final RunNotifier rn = new RunNotifier();
        final RunListener listener = new AdaptingRunListener(this.description, this.rc);
        rn.addFirstListener(listener);
        this.runner.run(rn);
    }
}
