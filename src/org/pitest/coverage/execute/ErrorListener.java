// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.coverage.execute;

import org.pitest.util.Log;
import java.util.logging.Level;
import org.pitest.testapi.TestResult;
import org.pitest.testapi.Description;
import java.util.logging.Logger;
import org.pitest.testapi.TestListener;

public class ErrorListener implements TestListener
{
    private static final Logger LOG;
    
    @Override
    public void onRunStart() {
    }
    
    @Override
    public void onTestStart(final Description d) {
    }
    
    @Override
    public void onTestFailure(final TestResult tr) {
        ErrorListener.LOG.log(Level.SEVERE, tr.getDescription().toString(), tr.getThrowable());
    }
    
    @Override
    public void onTestSkipped(final TestResult tr) {
    }
    
    @Override
    public void onTestSuccess(final TestResult tr) {
    }
    
    @Override
    public void onRunEnd() {
    }
    
    static {
        LOG = Log.getLogger();
    }
}
