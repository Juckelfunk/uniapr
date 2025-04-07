// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr.junit.runner;

import org.uniapr.commons.misc.MemberNameUtils;
import org.pitest.testapi.Description;
import java.util.List;
import org.pitest.testapi.ResultCollector;

class DefaultResultCollector implements ResultCollector
{
    private final List<String> failingTestNames;
    private Throwable throwable;
    
    public DefaultResultCollector(final List<String> failingTestNames) {
        this.failingTestNames = failingTestNames;
    }
    
    @Override
    public void notifyEnd(final Description description, final Throwable t) {
        if (t != null) {
            final String failingTestName = MemberNameUtils.sanitizeExtendedTestName(description.getName());
            this.failingTestNames.add(failingTestName);
            this.throwable = t;
            System.out.flush();
            System.err.println();
            t.printStackTrace();
            System.err.println();
            System.err.flush();
        }
    }
    
    @Override
    public void notifyEnd(final Description description) {
    }
    
    @Override
    public void notifyStart(final Description description) {
        final String testName = MemberNameUtils.sanitizeExtendedTestName(description.getName());
        System.out.println("RUNNING: " + testName + "... ");
    }
    
    @Override
    public void notifySkipped(final Description description) {
        final String testName = MemberNameUtils.sanitizeExtendedTestName(description.getName());
        System.out.println("SKIPPED: " + testName);
    }
    
    @Override
    public boolean shouldExit() {
        return false;
    }
    
    public boolean shouldRestart() {
        return this.throwable instanceof ExceptionInInitializerError;
    }
}
