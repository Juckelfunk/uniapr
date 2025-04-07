// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.testng;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.ITestListener;

class MutableTestListenerWrapper implements ITestListener, FailureTracker
{
    private TestNGAdapter child;
    
    public void setChild(final TestNGAdapter child) {
        this.child = child;
    }
    
    public boolean hasHadFailure() {
        return this.child.hasHadFailure();
    }
    
    public void onTestStart(final ITestResult result) {
        this.child.onTestStart(result);
    }
    
    public void onTestSuccess(final ITestResult result) {
        this.child.onTestSuccess(result);
    }
    
    public void onTestFailure(final ITestResult result) {
        this.child.onTestFailure(result);
    }
    
    public void onTestSkipped(final ITestResult result) {
        this.child.onTestSkipped(result);
    }
    
    public void onTestFailedButWithinSuccessPercentage(final ITestResult result) {
        this.child.onTestFailedButWithinSuccessPercentage(result);
    }
    
    public void onStart(final ITestContext context) {
        this.child.onStart(context);
    }
    
    public void onFinish(final ITestContext context) {
        this.child.onFinish(context);
    }
}
