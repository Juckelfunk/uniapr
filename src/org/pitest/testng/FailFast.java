// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.testng;

import org.testng.SkipException;
import org.testng.ITestResult;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;

class FailFast implements IInvokedMethodListener
{
    private final FailureTracker listener;
    
    FailFast(final FailureTracker listener) {
        this.listener = listener;
    }
    
    public void beforeInvocation(final IInvokedMethod method, final ITestResult testResult) {
        if (this.listener.hasHadFailure()) {
            throw new SkipException("Skipping");
        }
    }
    
    public void afterInvocation(final IInvokedMethod method, final ITestResult testResult) {
    }
}
