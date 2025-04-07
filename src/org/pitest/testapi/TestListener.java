// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.testapi;

public interface TestListener
{
    void onRunStart();
    
    void onTestStart(final Description p0);
    
    void onTestFailure(final TestResult p0);
    
    void onTestSkipped(final TestResult p0);
    
    void onTestSuccess(final TestResult p0);
    
    void onRunEnd();
}
