// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.testng;

import org.testng.ITestResult;
import org.testng.ITestContext;
import org.pitest.testapi.Description;
import org.pitest.testapi.ResultCollector;
import org.testng.ITestListener;

public class TestNGAdapter implements ITestListener
{
    private final ResultCollector rc;
    private final Description description;
    private final Class<?> clazz;
    private boolean hasHadFailure;
    private Throwable error;
    
    public TestNGAdapter(final Class<?> clazz, final Description d, final ResultCollector rc) {
        this.hasHadFailure = false;
        this.rc = rc;
        this.description = d;
        this.clazz = clazz;
    }
    
    public boolean hasHadFailure() {
        return this.hasHadFailure;
    }
    
    public void onFinish(final ITestContext arg0) {
        if (this.error != null) {
            this.rc.notifyEnd(this.description, this.error);
        }
        else {
            this.rc.notifyEnd(this.description);
        }
    }
    
    public void onStart(final ITestContext arg0) {
        this.rc.notifyStart(this.description);
    }
    
    public void onTestFailedButWithinSuccessPercentage(final ITestResult arg0) {
        this.rc.notifyEnd(this.makeDescription(arg0));
    }
    
    public void onTestFailure(final ITestResult arg0) {
        this.hasHadFailure = true;
        this.error = arg0.getThrowable();
        this.rc.notifyEnd(this.makeDescription(arg0), this.error);
    }
    
    public void onTestSkipped(final ITestResult arg0) {
        this.rc.notifySkipped(this.makeDescription(arg0));
    }
    
    public void onTestStart(final ITestResult arg0) {
        this.rc.notifyStart(this.makeDescription(arg0));
    }
    
    public void onTestSuccess(final ITestResult arg0) {
        this.rc.notifyEnd(this.makeDescription(arg0));
    }
    
    private Description makeDescription(final ITestResult result) {
        return new Description(result.getMethod().getMethodName(), this.clazz);
    }
}
