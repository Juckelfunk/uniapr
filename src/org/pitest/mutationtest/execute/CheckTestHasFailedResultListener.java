// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.execute;

import org.pitest.mutationtest.DetectionStatus;
import org.pitest.testapi.TestResult;
import org.pitest.testapi.Description;
import org.pitest.functional.Option;
import org.pitest.testapi.TestListener;

public class CheckTestHasFailedResultListener implements TestListener
{
    private Option<Description> lastFailingTest;
    private int testsRun;
    
    public CheckTestHasFailedResultListener() {
        this.lastFailingTest = (Option<Description>)Option.none();
        this.testsRun = 0;
    }
    
    @Override
    public void onTestFailure(final TestResult tr) {
        this.lastFailingTest = Option.some(tr.getDescription());
    }
    
    @Override
    public void onTestSkipped(final TestResult tr) {
    }
    
    @Override
    public void onTestStart(final Description d) {
        ++this.testsRun;
    }
    
    @Override
    public void onTestSuccess(final TestResult tr) {
    }
    
    public DetectionStatus status() {
        if (this.lastFailingTest.hasSome()) {
            return DetectionStatus.KILLED;
        }
        return DetectionStatus.SURVIVED;
    }
    
    public Option<Description> lastFailingTest() {
        return this.lastFailingTest;
    }
    
    public int getNumberOfTestsRun() {
        return this.testsRun;
    }
    
    @Override
    public void onRunEnd() {
    }
    
    @Override
    public void onRunStart() {
    }
}
